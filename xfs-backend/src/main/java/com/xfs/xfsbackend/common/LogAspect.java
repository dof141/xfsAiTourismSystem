package com.xfs.xfsbackend.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.xfs.xfsbackend.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String url = "";
        String httpMethod = "";
        String ip = "";
        String params = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            url = request.getRequestURI();
            httpMethod = request.getMethod();
            ip = request.getRemoteAddr();
            params = Arrays.toString(joinPoint.getArgs());
            if (params.length() > 200) {
                params = params.substring(0, 200) + "...";
            }
        }

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("[{}] {} {} | IP: {} | 参数: {} | 耗时: {}ms", httpMethod, url, methodName, ip, params, cost);
            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - start;
            log.error("[{}] {} {} | IP: {} | 参数: {} | 耗时: {}ms | 异常: {}", httpMethod, url, methodName, ip, params, cost, e.getMessage());
            throw e;
        }
    }
}
