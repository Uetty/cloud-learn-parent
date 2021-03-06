package com.uetty.cloud.feign.api.api.engine;

import com.uetty.cloud.feign.api.fallback.HystrixClient;
import com.uetty.cloud.feign.api.fallback.RedisLuaHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Redis Lua  脚本 API
 */
@FeignClient(value = "rule-engine",fallback = RedisLuaHystrix.class)
@RequestMapping("/redis")
public interface RedisLuaApi extends HystrixClient {

    @GetMapping("/classPut")
    Mono classPut(Integer userId,String userName);

    @GetMapping("/classGet")
    Mono classGet(Integer userId);

    @GetMapping("/getHashFromZset")
    Mono getHashFromZset();

    @GetMapping("/lock")
    Mono lock();
}
