package com.uetty.rule.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@EnableSwagger2WebFlux
@Configuration
public class SwaggerConfig {


    @Bean
    public Docket personApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("all")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.uetty.controller"))
                .paths(PathSelectors.any())
                .build();
    }

}
