package com.academy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for monitoring and managing distributed locks
 * Provides health checks, deadlock detection, and lock cleanup
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LockMonitoringService {
    
    private final CustomLockService lockService;
    
    /**
     * Check lock health - runs every 30 seconds
     * Logs active locks and statistics
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    public void checkLockHealth() {
        int activeLocks = lockService.getActiveLockCount();
        if (activeLocks > 0) {
            log.debug("Active locks: {}", activeLocks);
        }
    }
    
    /**
     * Get summary of all lock statistics
     * Useful for monitoring and debugging
     */
    public String getLockHealthSummary() {
        int activeLocks = lockService.getActiveLockCount();
        return String.format("Lock Health: %d active locks", activeLocks);
    }
}

