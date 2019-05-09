package com.uetty.rule.config.redis;

import com.uetty.rule.config.redis.template.RuleRedisTemplate;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 *  规则Redis
 */
@Configuration
public class RuleRedis {

    @ConfigurationProperties(prefix = "spring.redis.rule")
    public RuleRedisTemplate redisTemplate(RedisConfig redisConfig) {
        return new RuleRedisTemplate(connectionFactory(redisConfig));
    }

    public ReactiveRedisConnectionFactory connectionFactory(RedisConfig redisConfig) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(redisConfig.getDbIndex());
        configuration.setHostName(redisConfig.getHost());
        configuration.setPort(redisConfig.getPort());
        configuration.setPassword(redisConfig.getPassword());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        return factory;
    }


}
