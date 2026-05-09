package com.xfs.xfsbackend.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**") // 拦截所有接口
                .excludePathPatterns(
                        "/api/admin/login",      // 放行登录
                        "/api/tourist/login",    // 放行游客登录
                        "/api/area/list",       // 小程序需要拉取景区列表，暂时放行
                        "/api/area/hot",        // 放行热门
                        "/api/area/{id}/spots", // 放行子景点
                        "/api/ai/**"           // 暂时放行 AI (方便小程序使用)
                        // "/api/reserve/add"   // ❌ 不再放行，预约必须登录
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {


        // 配置全局跨域
        registry.addMapping("/**") // 所有接口
                .allowedOriginPatterns("*") // 支持所有域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持的方法
                .allowedHeaders("*")
                .allowCredentials(true) // 是否支持携带 Cookie
                .maxAge(3600); // 预检请求的有效期，单位秒
    }
}
