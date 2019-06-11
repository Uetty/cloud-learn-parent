package com.uetty.rule.service;

import com.google.common.collect.Lists;
import com.uetty.rule.config.redis.operations.ReactiveClassOperations;
import com.uetty.rule.config.redis.operations.ReactiveLockOperations;
import com.uetty.rule.config.redis.operations.ReactiveLuaOperations;
import com.uetty.rule.config.redis.template.RedisTemplateRule;
import com.uetty.rule.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHyperLogLogOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RedisService {

    private final RedisTemplateRule<String, User> redisTemplateRule;

    private final RedisTemplateRule<String, String> redisTemplateRule1;

    @Autowired
    public RedisService(RedisTemplateRule<String, User> redisTemplateRule, RedisTemplateRule<String, String> redisTemplateRule1) {
        this.redisTemplateRule = redisTemplateRule;
        this.redisTemplateRule1 = redisTemplateRule1;
    }

    public Mono<?> classPut(String key, Object value) {
        return redisTemplateRule.opsForClass().putClass(key, Lists.newArrayList(value, value));
    }

    public Mono<List<User>> getHashFromZset(String zsetKey, String hashKey, String start, String end) {
        ReactiveLuaOperations<String, User> lua = redisTemplateRule.opsForLua();
        return lua.getHashFromSortedSet(zsetKey, hashKey, 0, -1);
    }

    public Mono classGet(String key, Integer userId) {
        User user = new User();
        user.setUserId(userId);
        User user1 = new User();
        user1.setUserId(3);
        ReactiveClassOperations<String, String, User> classOperations = redisTemplateRule.opsForClass();
        return classOperations.getClass(key, user);
    }

    public Mono log() {
        ReactiveHyperLogLogOperations<String, String> hyperLogLog = redisTemplateRule1.opsForHyperLogLog();
        return hyperLogLog.add("key", "1", "2", "3", "3", "2")
                .flatMap(i -> hyperLogLog.size("key"));
    }

    public Mono<Boolean> redisLock(){
        ReactiveLockOperations lock = redisTemplateRule.opsForLock();
        return lock.tryLock("key");
    }
}
