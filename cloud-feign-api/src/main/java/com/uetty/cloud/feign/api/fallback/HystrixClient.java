package com.uetty.cloud.feign.api.fallback;

public interface HystrixClient {

    default String iFailSometimes() {
        return "";
    }
}
