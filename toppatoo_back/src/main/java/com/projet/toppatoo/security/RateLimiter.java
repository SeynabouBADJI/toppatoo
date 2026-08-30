package com.projet.toppatoo.security;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    private final ConcurrentHashMap<String, LoginAttempt> attempts = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final long LOCK_TIME = 15 * 60 * 1000; // 15 minutes

    public boolean isBlocked(String ip) {
        LoginAttempt attempt = attempts.get(ip);
        if (attempt == null) return false;
        return attempt.getLockTime() > System.currentTimeMillis();
    }

    public void recordAttempt(String ip) {
        LoginAttempt attempt = attempts.getOrDefault(ip, new LoginAttempt());
        attempt.incrementAttempts();
        
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            attempt.setLockTime(System.currentTimeMillis() + LOCK_TIME);
        }
        
        attempts.put(ip, attempt);
    }

    public void resetAttempts(String ip) {
        attempts.remove(ip);
    }
}

class LoginAttempt {
    private int attempts = 0;
    private long lockTime = 0;

    public void incrementAttempts() {
        this.attempts++;
    }

    public int getAttempts() {
        return attempts;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }
}