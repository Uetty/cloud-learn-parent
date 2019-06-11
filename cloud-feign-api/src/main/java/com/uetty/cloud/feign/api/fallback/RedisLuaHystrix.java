package com.uetty.cloud.feign.api.fallback;

import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.uetty.cloud.feign.api.api.engine.RedisLuaApi;
import reactor.core.publisher.Mono;

@Hystrix
public class RedisLuaHystrix implements RedisLuaApi {

    @Override
    public Mono classPut(Integer userId, String userName) {
        return null;
    }

    @Override
    public Mono classGet(Integer userId) {
        return null;
    }

    @Override
    public Mono getHashFromZset() {
        return null;
    }

    @Override
    public Mono lock() {
        return null;
    }
}
