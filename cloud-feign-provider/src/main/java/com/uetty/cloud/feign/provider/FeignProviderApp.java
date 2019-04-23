package com.uetty.cloud.feign.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.uetty.cloud.feign.provider.mapper")
public class FeignProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(FeignProviderApp.class, args);
    }

}
