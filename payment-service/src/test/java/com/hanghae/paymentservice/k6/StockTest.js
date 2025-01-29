import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    scenarios: {
        constant_users_test: {
            executor: 'constant-vus', // 일정한 수의 사용자 유지
            vus: 100, // 동시에 실행할 가상 사용자 수
            duration: '1m', // 테스트 실행 시간: 1분
        },
    },
};

// 5부터 1666까지의 orderId 리스트 생성
const orderIds = Array.from({ length: 99999 }, (_, i) => i + 5);

export default function () {
    // 1. 랜덤으로 orderId 선택
    const randomOrderId = orderIds[Math.floor(Math.random() * orderIds.length)];
    let orderEvent = JSON.stringify({
        orderId: randomOrderId,
        totalPrice: 100.00,
        orderRequestDtoList: [{ productId: 1, quantity: 1 }]
    });
    // 2. HTTP 요청 전송
    const url = `http://localhost:9005/test/stock/rock`;
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    const res = http.post(url,orderEvent, params); // POST 요청에 본문이 없으므로 `null` 추가

    // 3. 응답 상태 확인
    check(res, {
        '응답 코드가 200이다': (r) => r.status === 200,
    });

}
