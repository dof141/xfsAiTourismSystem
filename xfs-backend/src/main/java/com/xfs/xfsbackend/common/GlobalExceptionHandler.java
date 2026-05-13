package com.xfs.xfsbackend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 * 统一捕获 Controller 层抛出的异常，避免将堆栈、SQL 错误等敏感信息直接返回给前端。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常。
     * 这类异常通常代表业务执行过程中发生了未预期的问题。
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("系统运行异常: ", e);
        return Result.error("服务器开小差了，请稍后再试");
    }

    /**
     * 处理 Bean Validation 参数校验失败。
     * 将字段名和校验错误拼接成可读提示返回给前端。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.error(msg);
    }

    /**
     * 处理请求体 JSON 格式错误。
     * 常见场景是前端传入了非法 JSON 或字段类型不匹配。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error("请求体格式错误，请传入合法JSON");
    }

    /**
     * 处理缺少必填请求参数。
     * 例如接口要求 query 参数 date，但前端没有传。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error("缺少必填参数: " + e.getParameterName());
    }

    /**
     * 处理 HTTP 请求方法不支持。
     * 例如接口只支持 POST，但前端使用了 GET。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return Result.error("不支持的请求方法: " + e.getMethod());
    }

    /**
     * 兜底异常处理。
     * 捕获所有未被前面处理器覆盖的异常，并返回通用错误提示。
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统未知错误: ", e);
        return Result.error("系统遇到了一点小麻烦，请联系管理员");
    }
}
