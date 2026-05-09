package com.xfs.xfsbackend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHashGenerator {

    @Test
    void generateHashForSeedData() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        System.out.println("=== BCrypt Hash for '123456': " + hash);
        assertTrue(encoder.matches("123456", hash), "Hash verification failed");
        System.out.println("=== Verification passed!");
    }
}
