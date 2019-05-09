package com.uetty.rule.config.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RedisConfig {

    private int dbIndex;

    private String host;

    private int port;

    private String password;

    private int timeout;

}
