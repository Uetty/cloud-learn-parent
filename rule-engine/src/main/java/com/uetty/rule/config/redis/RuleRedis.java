package com.uetty.rule.config.redis;

import com.uetty.rule.config.redis.template.RedisTemplateRule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
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
    public RedisTemplateRule ruleRedisTemplate(RedisConfig ruleRedisConfig) {
        return new RedisTemplateRule(ruleConnectionFactory(ruleRedisConfig));
    }

    private ReactiveRedisConnectionFactory ruleConnectionFactory(RedisConfig ruleRedisConfig) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(ruleRedisConfig.getDbIndex());
        configuration.setHostName(ruleRedisConfig.getHost());
        configuration.setPort(ruleRedisConfig.getPort());
        configuration.setPassword(ruleRedisConfig.getPassword());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        return factory;
    }


}
