package com.academy.aspect;

import com.academy.annotation.WithLock;
import com.academy.exception.LockAcquisitionException;
import com.academy.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class LockAspect {

    private final DistributedLockService lockService;
    private final ExpressionParser spelParser = new SpelExpressionParser();

    @Around("@annotation(withLock)")
    public Object executeWithLock(ProceedingJoinPoint pjp, WithLock withLock) throws Throwable {
        String resolvedKey = evaluateLockKey(pjp, withLock.key());

        Duration holdTimeout = Duration.ofSeconds(withLock.timeUnit().toSeconds(withLock.timeout()));
        Duration acquireTimeout = Duration.ofSeconds(withLock.timeUnit().toSeconds(withLock.waitTimeout()));

        log.debug("Acquiring lock '{}' for {}", resolvedKey, pjp.getSignature().getName());

        DistributedLockService.LockHandle handle = lockService.acquireLockWithRetry(
            resolvedKey, holdTimeout, withLock.maxRetries(), acquireTimeout
        );

        if (handle == null) {
            log.warn("Could not acquire lock '{}' for {}", resolvedKey, pjp.getSignature().getName());
            if (withLock.throwOnFailure()) {
                throw new LockAcquisitionException(withLock.errorMessage());
            }
            log.info("Skipping execution of {} — lock unavailable and throwOnFailure=false", pjp.getSignature().getName());
            return null;
        }

        try {
            log.debug("Lock '{}' acquired, proceeding", resolvedKey);
            return pjp.proceed();
        } finally {
            boolean freed = lockService.releaseLock(handle);
            if (freed) {
                log.debug("Lock '{}' released", resolvedKey);
            } else {
                log.warn("Lock '{}' could not be released", resolvedKey);
            }
        }
    }

    // -------------------------------------------------------------------------

    private String evaluateLockKey(ProceedingJoinPoint pjp, String keyExpression) {
        try {
            EvaluationContext ctx = new StandardEvaluationContext();
            String[] paramNames = extractParamNames(pjp);
            Object[] args = pjp.getArgs();

            for (int i = 0; i < args.length; i++) {
                if (paramNames != null && i < paramNames.length) {
                    ctx.setVariable(paramNames[i], args[i]);
                }
                ctx.setVariable("arg" + i, args[i]);
            }

            Object value = spelParser.parseExpression(keyExpression).getValue(ctx);
            return value != null ? value.toString() : keyExpression;

        } catch (Exception ex) {
            log.warn("SpEL evaluation failed for key '{}': {} — using literal", keyExpression, ex.getMessage());
            return keyExpression;
        }
    }

    private String[] extractParamNames(ProceedingJoinPoint pjp) {
        try {
            return ((MethodSignature) pjp.getSignature()).getParameterNames();
        } catch (Exception ex) {
            log.debug("Could not extract parameter names: {}", ex.getMessage());
            return null;
        }
    }
}
