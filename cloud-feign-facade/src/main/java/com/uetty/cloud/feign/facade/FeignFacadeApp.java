package com.uetty.cloud.feign.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.uetty.cloud.feign.facade.api")
public class FeignFacadeApp {

    public static void main(String[] args) {
        SpringApplication.run(FeignFacadeApp.class, args);
    }
}
