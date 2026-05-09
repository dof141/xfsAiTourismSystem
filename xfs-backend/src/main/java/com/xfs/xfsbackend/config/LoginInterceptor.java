package com.xfs.xfsbackend.config;

import com.xfs.xfsbackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

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
            // token 校验失败
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\",\"data\":null}");
        return false;
    }
}
