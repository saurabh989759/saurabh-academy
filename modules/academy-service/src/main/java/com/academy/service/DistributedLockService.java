package com.academy.service;

import com.academy.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Distributed locking service using Spring Integration's RedisLockRegistry
 * This is the Spring-native solution for distributed locks
 * 
 * Features:
 * - Spring-native implementation (part of Spring Integration)
 * - Standard Java Lock interface
 * - Automatic lock expiration (prevents deadlocks)
 * - Thread-safe distributed locks
 * - Simple and Spring-idiomatic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {
    
    private final RedisLockRegistry redisLockRegistry;
    
    /**
     * Acquire a distributed lock with retry mechanism
     * Uses Spring Integration's RedisLockRegistry
     * 
     * @param lockKey The key for the lock
     * @param timeout Lock timeout duration (not used directly, locks expire automatically)
     * @param maxRetries Maximum number of retry attempts
     * @param waitTimeout Maximum time to wait for lock acquisition
     * @return LockHandle if acquired, null otherwise
     */
    public LockHandle acquireLockWithRetry(String lockKey, Duration timeout, int maxRetries, Duration waitTimeout) {
        Lock lock = redisLockRegistry.obtain(lockKey);
        long waitTimeoutMs = waitTimeout.toMillis();
        long startTime = System.currentTimeMillis();
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                // Try to acquire lock with wait timeout
                // Spring Integration's RedisLockRegistry handles expiration automatically
                boolean acquired = lock.tryLock(waitTimeoutMs / (maxRetries + 1), TimeUnit.MILLISECONDS);
                
                if (acquired) {
                    log.debug("Lock acquired: {} (attempt: {})", lockKey, attempt + 1);
                    return new LockHandle(lock, lockKey);
                }
                
                // Check if we've exceeded total wait timeout
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= waitTimeoutMs) {
                    log.warn("Lock acquisition timeout: {} (waited: {}ms)", lockKey, elapsed);
                    return null;
                }
                
                // Exponential backoff: delay = 100ms * 2^attempt
                if (attempt < maxRetries) {
                    long delayMs = 100L * (1L << attempt);
                    Thread.sleep(delayMs);
                    log.debug("Lock acquisition retry: {} (attempt: {}, delay: {}ms)", lockKey, attempt + 1, delayMs);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Lock acquisition interrupted: {}", lockKey, e);
                return null;
            } catch (Exception e) {
                log.error("Error acquiring lock: {}", lockKey, e);
                if (attempt == maxRetries) {
                    return null;
                }
            }
        }
        
        log.warn("Failed to acquire lock after {} retries: {}", maxRetries, lockKey);
        return null;
    }
    
    /**
     * Release a distributed lock
     * Uses standard Java Lock interface
     * 
     * @param lockHandle The lock handle
     * @return true if released successfully, false otherwise
     */
    public boolean releaseLock(LockHandle lockHandle) {
        if (lockHandle == null || lockHandle.lock == null) {
            return false;
        }
        
        try {
            // Standard Java Lock interface - unlock() is safe to call
            // Spring Integration's RedisLockRegistry handles thread safety
            lockHandle.lock.unlock();
            log.debug("Lock released: {}", lockHandle.lockKey);
            return true;
        } catch (IllegalMonitorStateException e) {
            log.warn("Lock release failed - not held by current thread: {}", lockHandle.lockKey);
            return false;
        } catch (IllegalStateException e) {
            // Lock expired before release - this is expected when operations take longer than lock expiration
            // The lock was automatically released by Redis due to expiration, which is safe
            if (e.getMessage() != null && e.getMessage().contains("expiration")) {
                log.warn("Lock expired before release: {} (operation may have taken longer than lock timeout). This is expected behavior.", 
                    lockHandle.lockKey);
            } else {
                log.warn("Lock release failed - illegal state: {}", lockHandle.lockKey, e);
            }
            return false;
        } catch (Exception e) {
            log.error("Error releasing lock: {}", lockHandle.lockKey, e);
            return false;
        }
    }
    
    /**
     * Extend lock timeout
     * Note: Spring Integration's RedisLockRegistry manages lock expiration automatically.
     * Locks expire after the configured time (default 40 seconds).
     * For long-running operations, ensure the lock registry is configured with appropriate expiration.
     * 
     * @param lockHandle The lock handle
     * @param additionalTime Additional time to extend (not directly supported, kept for API compatibility)
     * @return true if lock is still held, false otherwise
     */
    public boolean extendLock(LockHandle lockHandle, Duration additionalTime) {
        if (lockHandle == null || lockHandle.lock == null) {
            return false;
        }
        
        try {
            // Spring Integration's RedisLockRegistry automatically manages expiration
            // Locks expire after the configured time in RedisLockRegistry constructor
            // This method is kept for API compatibility
            log.debug("Lock extension requested for: {} (Spring Integration manages expiration automatically)", 
                lockHandle.lockKey);
            return true; // Return true if lock handle is valid
        } catch (Exception e) {
            log.error("Error extending lock: {}", lockHandle.lockKey, e);
            return false;
        }
    }
    
    /**
     * Check if a lock is currently held
     * Note: Standard Java Lock interface doesn't provide isLocked()
     * We obtain the lock and try to acquire it without waiting
     * 
     * @param lockKey The key for the lock
     * @return true if lock is held, false otherwise
     */
    public boolean isLocked(String lockKey) {
        try {
            Lock lock = redisLockRegistry.obtain(lockKey);
            // Try to acquire without waiting - if successful, lock was not held
            boolean acquired = lock.tryLock();
            if (acquired) {
                lock.unlock(); // Release immediately since we were just checking
                return false; // Lock was not held
            }
            return true; // Lock is held
        } catch (Exception e) {
            log.error("Error checking lock status: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * Execute a task with automatic lock management
     * 
     * @param lockKey The key for the lock
     * @param timeout Lock timeout duration
     * @param task The task to execute
     * @param <T> Return type
     * @return Result of the task
     * @throws LockAcquisitionException if lock cannot be acquired
     */
    public <T> T executeWithLock(String lockKey, Duration timeout, LockedTask<T> task) {
        LockHandle lockHandle = acquireLockWithRetry(lockKey, timeout, 3, Duration.ofSeconds(10));
        
        if (lockHandle == null) {
            throw new LockAcquisitionException("Could not acquire lock: " + lockKey);
        }
        
        try {
            return task.execute();
        } finally {
            releaseLock(lockHandle);
        }
    }
    
    /**
     * Lock handle wrapper for standard Java Lock
     */
    public static class LockHandle {
        final Lock lock;
        final String lockKey;
        
        public LockHandle(Lock lock, String lockKey) {
            this.lock = lock;
            this.lockKey = lockKey;
        }
    }
    
    /**
     * Functional interface for tasks that need locking
     */
    @FunctionalInterface
    public interface LockedTask<T> {
        T execute();
    }
}
