import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

// 사용자 정의 메트릭
let orderDuration = new Trend('order_duration'); // 주문 요청 시간 측정

// 환경 설정
export let options = {
    stages: [
        { duration: '1m', target: 800},  // 1분 동안 100명 동시 사용자 시뮬레이션
        // { duration: '2m', target: 200 },  // 2분 동안 200명 동시 사용자 시뮬레이션
        // { duration: '1m', target: 0 },    // 1분 동안 점차적으로 0명으로 감소
    ],
};

// 주문 생성 API 테스트
export default function () {
    const userId = Math.floor(Math.random() * 1000); // 가상의 userId 생성
    const requestBody = {
        orderRequestDtoList: [
            { productId: 28, quantity: 1 },
        ],
    };


    // 헤더 설정 (X-Claim-userId는 문자열로 변환하여 전달)
    const headers = {
        'Content-Type': 'application/json',
        'X-Claim-userId': userId.toString(),  // long 타입을 문자열로 변환
    };
    const url = 'http://localhost:9002/api/orders'; // 실제 API URL

    // POST 요청 보내기
    const response = http.post(url, JSON.stringify(requestBody), { headers: headers });
    // 응답 상태 코드 및 시간 체크
    check(response, {
        '응답 코드가 200이어야 한다': (r) => r.status === 200,
        // '응답 시간 2초 이내': (r) => r.timings.duration < 2000,  // 응답 시간 체크
    });


    // 요청 시간 기록
    orderDuration.add(response.timings.duration);

    // 1초 대기
    sleep(1);
}
