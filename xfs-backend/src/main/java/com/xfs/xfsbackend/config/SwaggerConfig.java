package com.xfs.xfsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Knife4j/Swagger API 文档配置。
 * 扫描 controller 包下的接口，生成后台接口文档页面，便于调试和答辩展示。
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建 OpenAPI 3 文档配置。
     * 指定接口扫描包和文档基础信息。
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xfs.xfsbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置 API 文档标题、描述和版本号。
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("雪峰山智慧文旅系统 API")
                .description("提供景区管理、预约核销、AI导览、数据统计等接口")
                .version("1.0")
                .build();
    }
}
