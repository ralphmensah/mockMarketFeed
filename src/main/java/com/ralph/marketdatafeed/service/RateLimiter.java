package com.ralph.marketdatafeed.service;

import com.ralph.marketdatafeed.enums.UserTier;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiter {
    private static final long FREE_RATE  = 10L;
    private static final long PRO_RATE   = 100L;


    private final Map<String, TokenBucket> buckets = Collections.synchronizedMap(
            new LinkedHashMap<String, TokenBucket>(100_000, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, TokenBucket> eldest) {
                    return size() > 100_000;
                }
            }
    );

    public boolean isAllowed(String userId, UserTier tier) {
        TokenBucket bucket = buckets.computeIfAbsent(userId, id -> createBucket(tier));
        return bucket.tryConsume();
    }

    private TokenBucket createBucket(UserTier tier) {
        long rate = (tier == UserTier.PRO) ? PRO_RATE : FREE_RATE;
        return new TokenBucket(rate, rate); // capacity == refill rate (1-second burst)
    }
    
}
