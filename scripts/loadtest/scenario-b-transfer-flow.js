import http from 'k6/http';
import { WebSocket } from 'k6/websockets';
import { check } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { URL_HOST, STOMP_PROTOCOL, HOSPITAL_MIN, mintToken, connectFrame, subscribeFrame, transferBody } from './lib.js';

// 시나리오 B — 통합 이송 흐름 (이송요청 → AI 분류 → 병원 fanout → 병원 응답 → 구급차 수신)
// 병원 WS(fanout 수신·응답 발사) + 구급차 WS(추천목록·응답 수신) + 이송요청 HTTP를 함께 발생시켜 접수·처리완료·전달을 측정한다.
// env: HOSPITALS · AMBULANCES · RATE · RAMP(이송 발생) · HOLD(유지) · WS_RAMP · TARGET
// 실행: k6 run -e HOSPITALS=527 -e AMBULANCES=1600 -e RATE=30 -e RAMP=30s -e HOLD=25s -e WS_RAMP=30s -e TARGET=<노드IP>:<ingress-port> scripts/loadtest/scenario-b-transfer-flow.js

const COORDS = JSON.parse(open('./hospital-coords.json')); // 병원 좌표 [lat,lon] 527개

const HOSPITALS = parseInt(__ENV.HOSPITALS || '527', 10);
const AMBULANCES = parseInt(__ENV.AMBULANCES || '1600', 10);
const RATE = parseInt(__ENV.RATE || '30', 10);
const RAMP = __ENV.RAMP || '60s';
const HOLD = __ENV.HOLD || '60s';
const WS_RAMP = __ENV.WS_RAMP || '30s';
const ACCEPT_RATE = parseFloat(__ENV.ACCEPT_RATE || '0.3');

const AMB_REQUESTED = '/user/queue/patient-transfers.requested-hospitals';
const AMB_RESPONSES = '/user/queue/patient-transfers.responses';
const HOSP_REQUESTS = '/user/queue/patient-transfers.requests';
const RESPONSE_URL = 'http://' + URL_HOST + '/api/v1/hospital/transfers/responses';
const TRANSFER_URL = 'http://' + URL_HOST + '/api/v1/transfers';

const MARKER = /LT\|[^|]*\|(\d+)/;
const AMB_ID = /"ambulance":\{"id":(\d+)/;

const transferSent = new Counter('transfer_sent');               // 이송요청 접수(202)
const fanoutReceived = new Counter('fanout_received');           // 병원 fanout 도달
const fanoutLatency = new Trend('fanout_latency_ms', true);      // 요청→병원 도달 지연
const hospitalResponded = new Counter('hospital_responded');     // 병원 응답 발사(200)
const ambReplyReceived = new Counter('amb_reply_received');      // 구급차 추천목록 도달
const ambResponseReceived = new Counter('amb_response_received'); // 구급차 응답 도달
const wsConnectFail = new Counter('ws_connect_fail');

const WS_HOLD = (parseInt(RAMP, 10) + parseInt(HOLD, 10)) + 's'; // 이송 발생 + drain 동안 세션 유지

export const options = {
  scenarios: {
    hospitals: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [{ duration: WS_RAMP, target: HOSPITALS }, { duration: WS_HOLD, target: HOSPITALS }],
      gracefulRampDown: '0s',
      gracefulStop: '5s',
      exec: 'hospital',
    },
    ambulances: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [{ duration: WS_RAMP, target: AMBULANCES }, { duration: WS_HOLD, target: AMBULANCES }],
      gracefulRampDown: '0s',
      gracefulStop: '5s',
      exec: 'ambulance',
    },
    transfers: {
      executor: 'constant-arrival-rate',
      rate: RATE,
      timeUnit: '1s',
      duration: RAMP,
      startTime: WS_RAMP,
      preAllocatedVUs: Math.max(50, RATE * 3),
      maxVUs: Math.max(200, RATE * 10),
      exec: 'transfer',
    },
  },
};

function decode(e) {
  return typeof e.data === 'string' ? e.data : String.fromCharCode.apply(null, new Uint8Array(e.data));
}

export function hospital() {
  const hid = HOSPITAL_MIN + (__VU % 527);
  const token = mintToken(hid, 'ROLE_HOSPITAL');
  let weClosed = false;
  const socket = new WebSocket('ws://' + URL_HOST + '/ws', STOMP_PROTOCOL);
  socket.onopen = () => socket.send(connectFrame(token));
  socket.onmessage = (e) => {
    const msg = decode(e);
    if (msg.indexOf('CONNECTED') === 0) {
      socket.send(subscribeFrame('h-' + __VU, HOSP_REQUESTS));
    } else if (msg.indexOf('MESSAGE') === 0) {
      const m = MARKER.exec(msg);
      if (m) {
        fanoutReceived.add(1);
        fanoutLatency.add(Date.now() - parseInt(m[1], 10));
      }
      const a = AMB_ID.exec(msg);
      if (a) {
        const res = http.post(RESPONSE_URL, JSON.stringify({ ambulanceId: parseInt(a[1], 10), accepted: Math.random() < ACCEPT_RATE }), {
          headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + token },
        });
        if (res.status === 200) hospitalResponded.add(1);
      }
    }
  };
  socket.onerror = () => { wsConnectFail.add(1); if (!weClosed) { weClosed = true; socket.close(); } };
  setTimeout(() => { weClosed = true; socket.close(); }, 600000);
}

export function ambulance() {
  const aid = 1 + (__VU % AMBULANCES);
  const token = mintToken(aid, 'ROLE_AMBULANCE');
  let weClosed = false;
  const socket = new WebSocket('ws://' + URL_HOST + '/ws', STOMP_PROTOCOL);
  socket.onopen = () => socket.send(connectFrame(token));
  socket.onmessage = (e) => {
    const msg = decode(e);
    if (msg.indexOf('CONNECTED') === 0) {
      socket.send(subscribeFrame('ar-' + __VU, AMB_REQUESTED));
      socket.send(subscribeFrame('as-' + __VU, AMB_RESPONSES));
    } else if (msg.indexOf('MESSAGE') === 0) {
      if (msg.indexOf('TRANSFER_RESPONSE') >= 0) ambResponseReceived.add(1);
      else ambReplyReceived.add(1);
    }
  };
  socket.onerror = () => { wsConnectFail.add(1); if (!weClosed) { weClosed = true; socket.close(); } };
  setTimeout(() => { weClosed = true; socket.close(); }, 600000);
}

export function transfer() {
  const aid = 1 + Math.floor(Math.random() * AMBULANCES);
  const token = mintToken(aid, 'ROLE_AMBULANCE');
  const coord = COORDS[(aid - 1) % COORDS.length];
  const name = 'LT|' + aid + '-' + __ITER + '|' + Date.now();
  const res = http.post(TRANSFER_URL, transferBody(name, coord), {
    headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + token },
  });
  if (check(res, { 'status 202': (r) => r.status === 202 })) transferSent.add(1);
}
