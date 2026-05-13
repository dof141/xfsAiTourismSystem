package com.xfs.xfsbackend.config;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员角色拦截器。
 * 只拦截带 @AdminRequired 的接口方法，校验登录拦截器写入 request 的 role 是否为 admin。
 */
@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    /**
     * 在 Controller 方法执行前检查管理员权限。
     * 普通游客访问管理员接口时返回 403，未标记 @AdminRequired 的接口直接放行。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AdminRequired adminRequired = handlerMethod.getMethodAnnotation(AdminRequired.class);

        if (adminRequired == null) {
            return true;
        }

        Object role = request.getAttribute("role");
        if ("admin".equals(role)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"权限不足：仅管理员可访问\",\"data\":null}");
        return false;
    }
}
