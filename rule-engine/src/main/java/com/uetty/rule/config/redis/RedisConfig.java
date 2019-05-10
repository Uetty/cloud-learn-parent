package com.uetty.rule.config.redis;

import lombok.Data;

@Data
public class RedisConfig {

    private Integer dbIndex;

    private String host;

    private Integer port;

    private String password;

    private Integer timeout;

}
