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

/**
 * Controller 请求日志切面。
 * 统一记录接口路径、请求方法、IP、参数和耗时，便于调试和排查接口问题。
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 环绕所有 controller 方法，记录请求开始到结束的耗时。
     * 如果接口抛出异常，也会记录异常摘要后继续抛给全局异常处理器。
     */
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
