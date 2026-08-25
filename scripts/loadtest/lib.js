import crypto from 'k6/crypto';
import encoding from 'k6/encoding';

// 무상태 HS256 시크릿. 각 서비스가 서명만 검증하므로 k6가 직접 토큰을 발급한다.
export const SECRET = 'local-dev-secret-key-change-me-please-256-bit';
// 진입점(ingress). TARGET=<노드IP>:<ingress-NodePort> 형태로 주입한다.
export const URL_HOST = __ENV.TARGET || '127.0.0.1';
// STOMP CONNECT 프레임의 host 헤더(형식상 값, 브로커 vhost와 무관).
export const STOMP_HOST = 'aiems.local';

export const HOSPITAL_MIN = 1001;
export const HOSPITAL_COUNT = 527;

export const DEST = '/user/queue/patient-transfers.requests';
const NULL = String.fromCharCode(0);

// Spring SubProtocolWebSocketHandler는 STOMP 서브프로토콜을 제시해야 STOMP 핸들러로 라우팅한다.
export const STOMP_PROTOCOL = 'v12.stomp';

function b64url(s) {
  return encoding.b64encode(s, 'rawurl');
}

export function mintToken(subject, role) {
  const header = b64url(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const now = Math.floor(Date.now() / 1000);
  const payload = b64url(JSON.stringify({ sub: String(subject), roles: [role], iat: now, exp: now + 3600 }));
  const signingInput = header + '.' + payload;
  const sig = crypto.hmac('sha256', SECRET, signingInput, 'base64rawurl');
  return signingInput + '.' + sig;
}

export function connectFrame(token) {
  return 'CONNECT\naccept-version:1.2\nheart-beat:0,0\nhost:' + STOMP_HOST + '\nAuthorization:Bearer ' + token + '\n\n' + NULL;
}

export function subscribeFrame(id, dest) {
  return 'SUBSCRIBE\nid:' + id + '\ndestination:' + dest + '\n\n' + NULL;
}

export function transferBody(name, coord) {
  return JSON.stringify({
    name: name,
    age: 55,
    gender: 'MALE',
    symptoms: '흉통',
    cause: '낙상',
    firstAid: '지혈',
    underlyingDisease: ['고혈압'],
    vitalSign: { minBloodPressure: 80, maxBloodPressure: 120, pulse: 88, respiratoryRate: 18, temperature: 36.7 },
    accidentLocation: { lat: coord[0], lon: coord[1], address: '부하테스트' },
  });
}
