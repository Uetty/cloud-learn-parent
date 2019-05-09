package com.uetty.rule.config.redis;

import com.uetty.rule.config.redis.template.RuleRedisTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * 规则Redis
 */
@Configuration
public class RuleRedis {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.rule")
    public RedisConfig ruleRedisConfig() {
        return new RedisConfig();
    }

    @Bean
    public RuleRedisTemplate redisTemplate() {
        return new RuleRedisTemplate(connectionFactory(ruleRedisConfig()));
    }

    public ReactiveRedisConnectionFactory connectionFactory(RedisConfig redisConfig) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(redisConfig.getDbIndex());
        configuration.setHostName(redisConfig.getHost());
        configuration.setPort(redisConfig.getPort());
        configuration.setPassword(redisConfig.getPassword());
        return new LettuceConnectionFactory(configuration);
    }


}
