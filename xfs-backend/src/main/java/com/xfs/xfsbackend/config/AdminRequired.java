package com.xfs.xfsbackend.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要管理员权限的接口方法
 * 在 Controller 方法上添加 @AdminRequired 即可限制仅管理员访问
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminRequired {
}
