package com.uetty.cloud.feign.api.api.engine;

import com.uetty.cloud.feign.api.fallback.HystrixClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "rule-engine",fallbackFactory = HystrixClientFallbackFactory.class)
public interface RepeatApi {
}
