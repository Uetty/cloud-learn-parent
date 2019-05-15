package com.uetty.rule.service;

import com.google.common.collect.Lists;
import com.uetty.rule.config.redis.script.ScriptConfig;
import com.uetty.rule.config.redis.template.RedisTemplateRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RedisService {

    private final RedisTemplateRule<String, Object> redisTemplateRule;

    @Autowired
    public RedisService(RedisTemplateRule<String, Object> redisTemplateRule) {
        this.redisTemplateRule = redisTemplateRule;
    }

    public Mono<?> hget(String key, Object value) {
        return redisTemplateRule.execute(ScriptConfig.getScript(ScriptConfig.ScriptType.HGET), Lists.newArrayList(key), Lists.newArrayList(value)).last();
    }

    public Mono<?> getHashFromZset(String zsetKey, String hashKey, String start, String end) {
        return redisTemplateRule.execute(ScriptConfig.<List>getScript(ScriptConfig.ScriptType.GET_HASH_FROM_ZSET),
                Lists.newArrayList(zsetKey, hashKey),
                Lists.newArrayList(start, end))
                .last();
    }
}
