package com.xfs.xfsbackend;

import com.xfs.xfsbackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtSecurityTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testTokenLifecycle() {
        // 1. 模拟生成 Token
        Long adminId = 1L;
        String username = "admin";
        String token = jwtUtils.generateToken(adminId, username);
        
        System.out.println("Generated Token: " + token);
        assertNotNull(token, "生成的 Token 不应为空");
        assertTrue(token.length() > 20, "Token 长度应正常");

        // 2. 解析 Token
        Claims claims = jwtUtils.parseToken(token);
        
        assertNotNull(claims, "解析后的 Claims 不应为空");
        assertEquals(username, claims.getSubject(), "用户名应匹配");
        assertEquals(adminId.intValue(), (Integer) claims.get("id"), "用户 ID 应匹配");
        
        System.out.println("Parsed Claims: " + claims);
    }

    @Test
    void testInvalidToken() {
        // 测试篡改后的 Token
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.fake.token";
        
        assertThrows(Exception.class, () -> {
            jwtUtils.parseToken(fakeToken);
        }, "错误的 Token 应该抛出异常");
    }
}
