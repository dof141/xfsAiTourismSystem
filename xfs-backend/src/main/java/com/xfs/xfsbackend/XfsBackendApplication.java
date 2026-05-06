package com.xfs.xfsbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xfs.xfsbackend.mapper") // 扫描路径更新为你的实际路径
public class XfsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(XfsBackendApplication.class, args);
    }

}
