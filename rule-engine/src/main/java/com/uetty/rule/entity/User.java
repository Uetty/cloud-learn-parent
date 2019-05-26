package com.uetty.rule.entity;

import com.uetty.rule.config.redis.annotation.RedisLazy;
import com.uetty.rule.config.redis.annotation.RedisPrimaryKey;
import lombok.Data;

@Data
@RedisLazy
public class User {

    @RedisPrimaryKey
    private Integer userId;

    private String userName;

}
