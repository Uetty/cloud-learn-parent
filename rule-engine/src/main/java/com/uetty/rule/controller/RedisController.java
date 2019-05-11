package com.uetty.rule.controller;

import com.uetty.rule.service.RedisService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @RequestMapping("/script")
    public Mono script() {
        return redisService.hget("user:detail", "1");
    }

    @RequestMapping("/getHashFromZset")
    public Mono getHashFromZset() {
        return redisService.getHashFromZset("user:sroce", "user:detail", "0", "-1");
    }
}
