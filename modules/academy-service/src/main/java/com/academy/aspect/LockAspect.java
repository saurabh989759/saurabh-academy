package com.academy.aspect;

import com.academy.annotation.WithLock;
import com.academy.exception.LockAcquisitionException;
import com.academy.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * AOP Aspect for @WithLock annotation
 * Automatically handles lock acquisition and release
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Execute before transaction aspects
public class LockAspect {
    
    private final DistributedLockService lockService;
    private final ExpressionParser parser = new SpelExpressionParser();
    
    @Around("@annotation(withLock)")
    public Object executeWithLock(ProceedingJoinPoint joinPoint, WithLock withLock) throws Throwable {
        // Resolve lock key using SpEL
        String lockKey = resolveLockKey(joinPoint, withLock.key());
        
        // Convert timeout values from TimeUnit to Duration
        long timeoutSeconds = withLock.timeUnit().toSeconds(withLock.timeout());
        long waitTimeoutSeconds = withLock.timeUnit().toSeconds(withLock.waitTimeout());
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        Duration waitTimeout = Duration.ofSeconds(waitTimeoutSeconds);
        
        log.debug("Attempting to acquire lock: {} for method: {}", lockKey, joinPoint.getSignature().getName());
        
        // Acquire lock with retry using Redisson
        DistributedLockService.LockHandle lockHandle = lockService.acquireLockWithRetry(
            lockKey, 
            timeout, 
            withLock.maxRetries(), 
            waitTimeout
        );
        
        if (lockHandle == null) {
            String errorMsg = withLock.errorMessage();
            log.warn("Failed to acquire lock: {} for method: {}", lockKey, joinPoint.getSignature().getName());
            
            if (withLock.throwOnFailure()) {
                throw new LockAcquisitionException(errorMsg);
            } else {
                log.info("Lock acquisition failed but throwOnFailure=false, skipping method execution");
                return null; // Skip method execution
            }
        }
        
        try {
            log.debug("Lock acquired: {} for method: {}", lockKey, joinPoint.getSignature().getName());
            // Execute the method
            return joinPoint.proceed();
        } finally {
            // Always release the lock
            boolean released = lockService.releaseLock(lockHandle);
            if (released) {
                log.debug("Lock released: {} for method: {}", lockKey, joinPoint.getSignature().getName());
            } else {
                log.warn("Failed to release lock: {} for method: {}", lockKey, joinPoint.getSignature().getName());
            }
        }
    }
    
    /**
     * Resolve lock key using SpEL expression
     */
    private String resolveLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        try {
            // Create evaluation context with method parameters
            EvaluationContext context = new StandardEvaluationContext();
            
            // Add method parameters to context
            String[] paramNames = getParameterNames(joinPoint);
            Object[] args = joinPoint.getArgs();
            
            for (int i = 0; i < args.length; i++) {
                if (paramNames != null && i < paramNames.length) {
                    context.setVariable(paramNames[i], args[i]);
                }
                // Also set by index for convenience
                context.setVariable("arg" + i, args[i]);
            }
            
            // Parse and evaluate expression
            Expression expression = parser.parseExpression(keyExpression);
            Object result = expression.getValue(context);
            
            return result != null ? result.toString() : keyExpression;
            
        } catch (Exception e) {
            log.warn("Failed to resolve lock key expression: {}, using as-is. Error: {}", keyExpression, e.getMessage());
            return keyExpression; // Fallback to literal string
        }
    }
    
    /**
     * Get parameter names from join point
     * Note: Requires -parameters compiler flag (already configured in build.gradle)
     */
    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        try {
            // Try to get parameter names from method signature
            org.aspectj.lang.reflect.MethodSignature signature = 
                (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
            return signature.getParameterNames();
        } catch (Exception e) {
            log.debug("Could not extract parameter names: {}", e.getMessage());
            return null;
        }
    }
}

