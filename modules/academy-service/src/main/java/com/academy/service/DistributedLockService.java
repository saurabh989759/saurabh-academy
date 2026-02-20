package com.academy.service;

import com.academy.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {

    private final RedisLockRegistry redisLockRegistry;

    public LockHandle acquireLockWithRetry(String lockKey, Duration timeout, int maxRetries, Duration waitTimeout) {
        Lock lock = redisLockRegistry.obtain(lockKey);
        long totalWaitMs = waitTimeout.toMillis();
        long startedAt = System.currentTimeMillis();
        long sliceMs = totalWaitMs / (maxRetries + 1);

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (lock.tryLock(sliceMs, TimeUnit.MILLISECONDS)) {
                    log.debug("Lock acquired: '{}' on attempt {}", lockKey, attempt + 1);
                    return new LockHandle(lock, lockKey);
                }

                long elapsed = System.currentTimeMillis() - startedAt;
                if (elapsed >= totalWaitMs) {
                    log.warn("Lock wait timeout exceeded for '{}' after {}ms", lockKey, elapsed);
                    return null;
                }

                if (attempt < maxRetries) {
                    long backoff = 100L * (1L << attempt);
                    Thread.sleep(backoff);
                    log.debug("Retrying lock '{}' attempt={} backoff={}ms", lockKey, attempt + 1, backoff);
                }

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for lock '{}'", lockKey);
                return null;
            } catch (Exception ex) {
                log.error("Error trying to acquire lock '{}': {}", lockKey, ex.getMessage());
                if (attempt == maxRetries) return null;
            }
        }

        log.warn("Lock '{}' not acquired after {} retries", lockKey, maxRetries);
        return null;
    }

    public boolean releaseLock(LockHandle handle) {
        if (handle == null || handle.lock == null) return false;

        try {
            handle.lock.unlock();
            log.debug("Lock '{}' released", handle.lockKey);
            return true;
        } catch (IllegalMonitorStateException ex) {
            log.warn("Lock '{}' not held by current thread", handle.lockKey);
            return false;
        } catch (IllegalStateException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("expiration")) {
                log.warn("Lock '{}' expired before explicit release", handle.lockKey);
            } else {
                log.warn("Illegal state releasing lock '{}': {}", handle.lockKey, ex.getMessage());
            }
            return false;
        } catch (Exception ex) {
            log.error("Unexpected error releasing lock '{}'", handle.lockKey, ex);
            return false;
        }
    }

    public boolean extendLock(LockHandle handle, Duration additionalTime) {
        if (handle == null || handle.lock == null) return false;
        log.debug("Lock extension requested for '{}' (managed by registry)", handle.lockKey);
        return true;
    }

    public boolean isLocked(String lockKey) {
        try {
            Lock lock = redisLockRegistry.obtain(lockKey);
            if (lock.tryLock()) {
                lock.unlock();
                return false;
            }
            return true;
        } catch (Exception ex) {
            log.error("Error checking lock status for '{}': {}", lockKey, ex.getMessage());
            return false;
        }
    }

    public <T> T executeWithLock(String lockKey, Duration timeout, LockedTask<T> task) {
        LockHandle handle = acquireLockWithRetry(lockKey, timeout, 3, Duration.ofSeconds(10));
        if (handle == null) {
            throw new LockAcquisitionException("Unable to acquire lock: " + lockKey);
        }
        try {
            return task.execute();
        } finally {
            releaseLock(handle);
        }
    }

    // ── Inner types ───────────────────────────────────────────────────────────

    public static class LockHandle {
        final Lock lock;
        final String lockKey;

        public LockHandle(Lock lock, String lockKey) {
            this.lock = lock;
            this.lockKey = lockKey;
        }
    }

    @FunctionalInterface
    public interface LockedTask<T> {
        T execute();
    }
}
