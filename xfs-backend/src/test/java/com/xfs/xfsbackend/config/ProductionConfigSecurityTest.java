package com.xfs.xfsbackend.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionConfigSecurityTest {

    private static final Path PROD_CONFIG = Paths.get("src/main/resources/application-prod.yml");

    @Test
    void applicationProdExistsAndDoesNotContainWeakDefaultSecrets() throws IOException {
        assertTrue(Files.exists(PROD_CONFIG), "生产环境必须提供 application-prod.yml");

        String yaml = new String(Files.readAllBytes(PROD_CONFIG), StandardCharsets.UTF_8);

        assertFalse(yaml.contains("20201211"), "生产数据库密码不能保留本地默认弱口令");
        assertFalse(yaml.contains("XFS_SECRET_2026_GRADUATION_PROJECT_DEFAULT"), "生产 JWT 不能保留默认密钥");
        assertFalse(yaml.contains("your-real-key"), "生产 AI Key 不能保留占位默认值");
        assertFalse(yaml.contains("admin123"), "生产 Druid 不能保留默认弱口令");
        assertFalse(Pattern.compile("\\$\\{DB_PASSWORD:[^}]+}").matcher(yaml).find(),
                "生产 DB_PASSWORD 必须由环境变量强制提供，不能配置冒号默认值");
        assertFalse(Pattern.compile("\\$\\{JWT_SECRET:[^}]+}").matcher(yaml).find(),
                "生产 JWT_SECRET 必须由环境变量强制提供，不能配置冒号默认值");
        assertFalse(Pattern.compile("\\$\\{AI_API_KEY:[^}]+}").matcher(yaml).find(),
                "生产 AI_API_KEY 必须由环境变量强制提供，不能配置冒号默认值");
    }
}
