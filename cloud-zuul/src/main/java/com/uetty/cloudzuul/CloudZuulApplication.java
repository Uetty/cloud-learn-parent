package com.uetty.cloudzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringCloudApplication
@RestController
public class CloudZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudZuulApplication.class, args);
    }


    private static String SERVICE_ERROR = "SERVICE ERROR";

    @RequestMapping("/global/hystrix")
    public Mono<String> defaultFallback() {
        return Mono.just(SERVICE_ERROR);
    }
}
