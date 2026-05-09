package com.xfs.xfsbackend.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.xfs.xfsbackend.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        Object result = joinPoint.proceed();

        long cost = System.currentTimeMillis() - start;
        log.info("[{}] 耗时: {}ms", methodName, cost);
        return result;
    }
}
