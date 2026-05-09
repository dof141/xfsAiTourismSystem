package com.xfs.xfsbackend.config;

import com.xfs.xfsbackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器 - 校验 JWT 令牌
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从请求头获取 token
        String token = request.getHeader("Authorization");

        // 3. 校验 token
        try {
            if (token != null && !token.isEmpty()) {
                // 如果是以 "Bearer " 开头则截取
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                Claims claims = jwtUtils.parseToken(token);
                // 校验通过，可以将 adminId 存入 request 方便后续使用
                request.setAttribute("adminId", claims.get("id"));
                return true;
            }
        } catch (Exception e) {
            // 校验失败
        }

        // 4. 校验不通过，返回 401 状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Please login first.");
        return false;
    }
}
