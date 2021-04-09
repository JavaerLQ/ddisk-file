package io.ddisk.config.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info().title("丢丢网盘 API")
                        .description("基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。")
                        .version("1.0.0")
                        .license(new License().name("MIT").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("丢丢网盘gitee地址")
                        .url("https://gitee.com/JavaerLi/ddisk"))
                ;
    }

}
