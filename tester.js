import http from 'k6/http';
import { check } from 'k6';

export const options = {
    // Simulate 1,000 active users constantly hitting the API for 30 seconds
    vus: 1000,
    duration: '30s',
};

export default function () {
    // Pick a random user ID between 1 and 100
    const userId = Math.floor(Math.random() * 100) + 1;

    const params = {
        headers: { 'userId': `user-${userId}`,
                    'userTier': "PRO"
        },
    };

    const res = http.get('http://localhost:8080/api/v1/marketfeed/BTCUSDT', params);

    // We expect responses to be either 200 (OK) or 429 (Rate Limited)
    check(res, {
        'is status 200 or 429': (r) => r.status === 200 || r.status === 429,
    });
}