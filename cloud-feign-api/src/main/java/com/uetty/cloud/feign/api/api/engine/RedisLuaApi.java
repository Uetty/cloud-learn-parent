package com.uetty.cloud.feign.api.api.engine;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Redis Lua  脚本 API
 */
@FeignClient("rule-engine")
@RequestMapping("/redis")
public interface RedisLuaApi {

    @GetMapping("/script")
    Mono script();

    @GetMapping("/getHashFromZset")
    Mono getHashFromZset();
}
