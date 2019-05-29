package com.uetty.rule.entity;

import com.uetty.rule.config.redis.annotation.RedisKey;
import com.uetty.rule.config.redis.annotation.RedisPrimaryKey;
import lombok.Data;

@Data
@RedisKey("user:detail")
public class User {

    @RedisPrimaryKey
    private Integer userId;

    private String userName;

}
