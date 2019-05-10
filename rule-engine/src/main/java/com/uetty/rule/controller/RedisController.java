package com.uetty.rule.controller;

import com.uetty.rule.config.redis.template.RedisTemplateRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisTemplateRule redisTemplateRule;

    @Autowired
    public RedisController(RedisTemplateRule redisTemplateRule) {
        this.redisTemplateRule = redisTemplateRule;
    }

    @RequestMapping("/script")
    public Flux<Object> script() {
        return redisTemplateRule.execute(RedisScript.of("eval \"return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}\" 2 key1 key2 first second"));
    }
}
