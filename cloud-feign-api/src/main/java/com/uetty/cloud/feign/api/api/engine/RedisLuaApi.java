package com.uetty.cloud.feign.api.api.engine;

import com.uetty.cloud.feign.api.fallback.HystrixClient;
import com.uetty.cloud.feign.api.fallback.HystrixClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Redis Lua  脚本 API
 */
@FeignClient(value = "rule-engine",fallbackFactory = HystrixClientFallbackFactory.class)
@RequestMapping("/redis")
public interface RedisLuaApi extends HystrixClient {

    @GetMapping("/script")
    Mono script(Integer userId,String userName);

    @GetMapping("/getHashFromZset")
    Mono getHashFromZset();
}
