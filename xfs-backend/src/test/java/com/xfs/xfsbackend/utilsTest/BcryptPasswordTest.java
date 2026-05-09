package com.xfs.xfsbackend.utilsTest;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BCrypt 密码加密测试
 * 验证密码加密和校验逻辑的正确性
 */
class BcryptPasswordTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void testEncodeAndMatch() {
        String rawPassword = "123456";
        String hash = encoder.encode(rawPassword);

        assertNotNull(hash, "哈希值不应为空");
        assertTrue(hash.startsWith("$2a$"), "BCrypt 哈希应以 $2a$ 开头");
        assertTrue(encoder.matches(rawPassword, hash), "原始密码应与哈希匹配");
    }

    @Test
    void testWrongPasswordNotMatch() {
        String hash = encoder.encode("123456");

        assertFalse(encoder.matches("wrongpassword", hash), "错误密码不应匹配");
        assertFalse(encoder.matches("", hash), "空密码不应匹配");
        assertFalse(encoder.matches("1234567", hash), "相似密码不应匹配");
    }

    @Test
    void testSamePasswordGeneratesDifferentHashes() {
        String password = "123456";
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);

        assertNotEquals(hash1, hash2, "同一密码的两次哈希应不同（盐值不同）");
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }

    @Test
    void testSeedDataHashIsValid() {
        // 验证 SQL 种子数据中使用的 BCrypt 哈希
        String seedHash = "$2a$10$Xi3R0HGIewq22zVaT4x3Ue.JH.hjCHbblLah.K75KV..JbUzN1aTK";

        assertTrue(encoder.matches("123456", seedHash),
                "SQL 种子数据的 BCrypt 哈希应与 '123456' 匹配");
        assertFalse(encoder.matches("admin", seedHash),
                "种子数据哈希不应匹配其他密码");
    }
}
