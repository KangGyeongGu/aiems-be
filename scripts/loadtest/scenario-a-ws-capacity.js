import { WebSocket } from 'k6/websockets';
import { Counter } from 'k6/metrics';
import { URL_HOST, STOMP_PROTOCOL, HOSPITAL_MIN, HOSPITAL_COUNT, DEST, mintToken, connectFrame, subscribeFrame } from './lib.js';

// 시나리오 A — 동시 WS 세션 수용 한계
// ramping-vus로 현실적 접속 속도로 램프해 목표 세션까지 확립 후 유지한다.
// env: MAX(목표 세션) · RAMP · HOLD · TARGET
// 실행: k6 run -e MAX=10000 -e RAMP=100s -e HOLD=60s -e TARGET=<노드IP>:<ingress-port> scripts/loadtest/scenario-a-ws-capacity.js

const MAX = parseInt(__ENV.MAX || '5000', 10);
const RAMP = __ENV.RAMP || '60s';
const HOLD = __ENV.HOLD || '40s';

const opened = new Counter('ws_opened');              // WS 핸드셰이크 성공
const connectFail = new Counter('ws_connect_fail');   // 접속 실패
const subscribed = new Counter('stomp_subscribed');   // STOMP CONNECTED(앱 도달)
const dropped = new Counter('ws_dropped');            // 유지 중 끊김

export const options = {
  scenarios: {
    ramp: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: RAMP, target: MAX },
        { duration: HOLD, target: MAX },
      ],
      gracefulRampDown: '0s',
      gracefulStop: '5s',
    },
  },
};

export default function () {
  const hid = HOSPITAL_MIN + (__VU % HOSPITAL_COUNT);
  const token = mintToken(hid, 'ROLE_HOSPITAL');
  let isOpen = false;
  let weClosed = false;

  const socket = new WebSocket('ws://' + URL_HOST + '/ws', STOMP_PROTOCOL);
  socket.onopen = function () {
    isOpen = true;
    opened.add(1);
    socket.send(connectFrame(token));
  };
  socket.onmessage = function (e) {
    const msg = typeof e.data === 'string' ? e.data : String.fromCharCode.apply(null, new Uint8Array(e.data));
    if (msg.indexOf('CONNECTED') === 0) {
      subscribed.add(1);
      socket.send(subscribeFrame('sub-' + __VU, DEST));
    }
  };
  socket.onerror = function () {
    if (!isOpen) connectFail.add(1);
    if (!weClosed) { weClosed = true; socket.close(); }
  };
  socket.onclose = function () {
    if (!weClosed) dropped.add(1);
  };
  setTimeout(function () {
    weClosed = true;
    socket.close();
  }, 600000);
}
