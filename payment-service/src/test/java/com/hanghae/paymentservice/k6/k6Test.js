import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    scenarios: {
        constant_users_test: {
            executor: 'constant-vus', // 일정한 수의 사용자 유지
            vus: 1660, // 동시에 실행할 가상 사용자 수
            duration: '1m', // 테스트 실행 시간: 1분
        },
    },
};

// 5부터 1666까지의 orderId 리스트 생성
const orderIds = Array.from({ length: 1662 }, (_, i) => i + 5);

export default function () {
    // 1. 랜덤으로 orderId 선택
    const randomOrderId = orderIds[Math.floor(Math.random() * orderIds.length)];

    // 2. HTTP 요청 전송
    const url = `http://localhost:9005/api/payment/process-payment?orderId=${randomOrderId}`;
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    const res = http.post(url, null, params); // POST 요청에 본문이 없으므로 `null` 추가

    // 3. 응답 상태 확인
    check(res, {
        '응답 코드가 200이다': (r) => r.status === 200,
    });

    // 4. 요청 간 딜레이 없음 (1분 내 최대 실행)
}
