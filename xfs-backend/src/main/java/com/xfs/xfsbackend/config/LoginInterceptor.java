package com.xfs.xfsbackend.config;

import com.xfs.xfsbackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录认证拦截器。
 * 负责解析 Authorization 请求头中的 JWT，并把用户 id 和角色写入 request 属性供后续业务使用。
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 在接口执行前校验登录态。
     * OPTIONS 预检请求直接放行；有效 JWT 放行；无 token 或 token 无效则返回 401。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        try {
            if (token != null && !token.isEmpty()) {
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                Claims claims = jwtUtils.parseToken(token);
                request.setAttribute("adminId", claims.get("id"));
                request.setAttribute("role", claims.get("role"));
                return true;
            }
        } catch (Exception e) {
            log.warn("token校验失败: {}, URI: {}", e.getMessage(), request.getRequestURI());
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\",\"data\":null}");
        return false;
    }
}
