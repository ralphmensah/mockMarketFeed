package com.ralph.marketdatafeed.service;

import jakarta.annotation.PostConstruct;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {

    private final long capacity;
    private final long refillRatePerSecond;
    private long availableTokens;
    private long lastRefillNanos;

    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucket(long capacity, long refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.availableTokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }


    /**
     * Attempts to consume one token.
     */
    public boolean tryConsume() {
        lock.lock();
        try {
            refill(); // Calculate tokens before trying to consume

            if (availableTokens > 0) {
                availableTokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRefillNanos;

        // Calculate how many tokens earned based on time passed
        long tokensToAdd = (elapsedNanos * refillRatePerSecond) / 1_000_000_000L;

        if (tokensToAdd > 0) {
            // Add tokens, but never exceed the max capacity
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillNanos = now;
        }
    }
}
