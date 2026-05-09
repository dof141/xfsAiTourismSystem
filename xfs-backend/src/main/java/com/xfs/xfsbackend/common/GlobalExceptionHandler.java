package com.xfs.xfsbackend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 拦截所有 Controller 抛出的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有的运行时异常 (RuntimeException)
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        // 1. 在后端控制台打印出详细的错误日志，方便你排查
        log.error("系统运行异常: ", e);

        // 2. 返回给前端一个友好的 JSON
        return Result.error("服务器开小差了，请稍后再试：" + e.getMessage());
    }

    /**
     * 捕获所有其他未知异常 (Exception)
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统未知错误: ", e);
        return Result.error("系统遇到了一点小麻烦，请联系管理员");
    }
}
