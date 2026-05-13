package com.xfs.xfsbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 全局配置。
 * 统一注册登录/管理员权限拦截器，并配置本地开发跨域访问规则。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private AdminRoleInterceptor adminRoleInterceptor;

    /**
     * 注册接口拦截器。
     * 登录接口、公开景区接口、AI 问答和 Swagger 文档接口不需要登录，其余 /api/** 默认需要 JWT。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 登录拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/admin/login",
                        "/api/tourist/login",
                        "/api/tourist/sendCode",
                        "/api/area/list",
                        "/api/area/hot",
                        "/api/area/{id}/spots",
                        "/api/ai/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**"
                );

        // 2. 管理员角色拦截器（在登录拦截器之后执行）
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/api/**");
    }

    /**
     * 配置跨域规则。
     * 当前允许 localhost 和 127.0.0.1 的任意端口访问，方便管理端和小程序本地联调。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
