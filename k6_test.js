import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'https://api.conx.co.kr';

export const options = {
    stages: [
        { duration: '30s', target: 10 },
        { duration: '1m', target: 20 },
        { duration: '2m', target: 30 }, // 예상 최대 동접
        { duration: '1m', target: 50 }, // 여유 용량 확인
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

export default function () {
    // 0~9 페이지 중 랜덤 조회
    const page = Math.floor(Math.random() * 10);

    const res = http.get(
        `${BASE_URL}/api/v1/projects?page=${page}&size=10`,
        {
            tags: {
                endpoint: 'project-list',
            },
        }
    );

    // 응답 검증
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.3);
}