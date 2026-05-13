package com.xfs.xfsbackend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类。
 * 负责生成和解析管理员/游客登录令牌，令牌中保存用户 id、用户名和角色等身份信息。
 */
@Component
public class JwtUtils {

    @Value("${xfs.jwt.secret}")
    private String secret;

    @Value("${xfs.jwt.expiration}")
    private long expiration;

    /**
     * 生成不带角色的兼容令牌。
     * 主要用于旧测试或历史兼容，新业务建议优先使用带 role 的重载方法。
     */
    public String generateToken(Long id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 生成带角色信息的令牌。
     * 管理员登录会写入 role=admin，游客登录会写入 role=tourist，后续权限拦截器依赖该字段判断权限。
     */
    public String generateToken(Long id, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 解析并校验 JWT Token。
     * 如果签名错误、格式错误或令牌过期，会抛出异常，由登录拦截器统一处理为未登录。
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
