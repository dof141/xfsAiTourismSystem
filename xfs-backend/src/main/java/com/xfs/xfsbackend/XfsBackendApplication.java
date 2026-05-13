package com.xfs.xfsbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 雪峰山智慧文旅系统后端启动类。
 * 负责启动 Spring Boot 应用，并通过 @MapperScan 扫描 MyBatis-Plus Mapper 接口。
 */
@SpringBootApplication
@MapperScan("com.xfs.xfsbackend.mapper") // 扫描路径更新为你的实际路径
public class XfsBackendApplication {

    /**
     * 后端服务入口方法。
     * 在 IDEA 或命令行启动该方法后，系统会加载配置、初始化 Spring 容器并启动内置 Tomcat。
     */
    public static void main(String[] args) {
        SpringApplication.run(XfsBackendApplication.class, args);
    }

}
