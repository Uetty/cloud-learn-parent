package com.uetty.rule.controller;

import com.uetty.cloud.feign.api.api.engine.RedisLuaApi;
import com.uetty.rule.entity.User;
import com.uetty.rule.service.RedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/redis")
public class RedisController implements RedisLuaApi {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/classPut")
    public Mono classPut(Integer userId, String userName) {
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        return redisService.classPut("user:detail", user);
    }

    @GetMapping("/classGet")
    public Mono classGet(Integer userId) {
        return redisService.classGet("user:detail", userId);
    }

    ;

    @GetMapping("/log")
    public Mono classGet() {
        return redisService.log();
    }

    ;

    @GetMapping("/getHashFromZset")
    public Mono getHashFromZset() {
        return redisService.getHashFromZset("user:sroce", "user:detail", "0", "-1");
    }

    public static void main(String[] args) throws InterruptedException {
        Mono.delay(Duration.ofMillis(500))
                .doOnSuccess(a -> System.out.println(1))
                .repeat()
                .subscribe();
        Thread.sleep(10000L);
    }

    @Override
    @GetMapping("/lock")
    public Mono lock() {
        return redisService.redisLock();
    }
}
