package com.uetty.cloudzuul.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码上路由配置
 */
@Configuration
public class RouteConfig {

    private static String HYSTRIX_NAME = "GLOBAL_HYSTRIX_NAME";

    private static String HYSTRIX_URI = "forward:/global/hystrix";

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/**")
                        .filters(f -> f.hystrix(config ->
                                config.setName(HYSTRIX_NAME)
                                        .setFallbackUri(HYSTRIX_URI))
                        ).uri(""))
                .build();
    }

}
