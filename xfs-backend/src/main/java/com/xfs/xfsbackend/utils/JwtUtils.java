package com.xfs.xfsbackend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // 密钥，建议生产环境放到配置文件
    private static final String SECRET = "XFS_SECRET_2026_GRADUATION_PROJECT";
    // 过期时间：7天
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成令牌
     */
    public String generateToken(Long adminId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", adminId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 解析令牌
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
