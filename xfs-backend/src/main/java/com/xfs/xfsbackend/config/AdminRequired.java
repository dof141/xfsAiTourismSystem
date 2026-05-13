package com.xfs.xfsbackend.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员权限标记注解。
 * 在 Controller 方法上添加 @AdminRequired 后，AdminRoleInterceptor 会校验当前 JWT 是否为管理员角色。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminRequired {
}
