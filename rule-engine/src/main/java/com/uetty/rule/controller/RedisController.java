package com.uetty.rule.controller;

import com.uetty.cloud.feign.api.api.engine.RedisLuaApi;
import com.uetty.rule.service.RedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/redis")
public class RedisController implements RedisLuaApi {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/script")
    public Mono script() {
        return redisService.hget("user:detail", "1");
    }

    @GetMapping("/getHashFromZset")
    public Mono getHashFromZset() {
        return redisService.getHashFromZset("user:sroce", "user:detail", "0", "-1");
    }
}
