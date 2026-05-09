package com.xfs.xfsbackend.utilsTest;

import com.xfs.xfsbackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 角色令牌测试（无 Spring 上下文依赖）
 */
class JwtRoleTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // 手动注入配置值（模拟 application.yml）
        ReflectionTestUtils.setField(jwtUtils, "secret", "XFS_TEST_SECRET_KEY_FOR_UNIT_TESTS_2026");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 604800000L);
    }

    @Test
    void testAdminTokenContainsRole() {
        String token = jwtUtils.generateToken(1L, "admin", "admin");
        Claims claims = jwtUtils.parseToken(token);

        assertEquals(1, ((Number) claims.get("id")).intValue());
        assertEquals("admin", claims.getSubject());
        assertEquals("admin", claims.get("role"));
    }

    @Test
    void testTouristTokenContainsRole() {
        String token = jwtUtils.generateToken(888L, "Tourist_1234", "tourist");
        Claims claims = jwtUtils.parseToken(token);

        assertEquals(888, ((Number) claims.get("id")).intValue());
        assertEquals("Tourist_1234", claims.getSubject());
        assertEquals("tourist", claims.get("role"));
    }

    @Test
    void testTokenWithoutRoleBackwardsCompatible() {
        String token = jwtUtils.generateToken(1L, "admin");
        Claims claims = jwtUtils.parseToken(token);

        assertEquals(1, ((Number) claims.get("id")).intValue());
        assertEquals("admin", claims.getSubject());
        assertNull(claims.get("role"), "无 role 参数生成的 token 应不含 role claim");
    }

    @Test
    void testInvalidTokenThrowsException() {
        assertThrows(Exception.class, () -> {
            jwtUtils.parseToken("invalid.token.value");
        });
    }

    @Test
    void testTokenExpirationIsValid() {
        String token = jwtUtils.generateToken(1L, "admin", "admin");
        Claims claims = jwtUtils.parseToken(token);

        assertNotNull(claims.getExpiration(), "Token 应有过期时间");
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis(),
                "Token 过期时间应在未来");
    }
}
