package com.uetty.rule.config.redis.script;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class ScriptConfig {

    private final ConfigurableApplicationContext applicationContext;

    @Value("${spring.redis.luaPath}")
    private String luaPath;

    @Autowired
    public ScriptConfig(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 初始化lua脚本
     */
    @PostConstruct
    public void initScript() {
        String path = this.getClass().getResource(luaPath).getPath();
        File file = new File(path);
        if (file.isDirectory()) {
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            String[] luaFiles = file.list();
            for (int i = 0; i < luaFiles.length; i++) {
                String name = luaFiles[i];
                DefaultRedisScript redisScript = new DefaultRedisScript();
                redisScript.setLocation(new ClassPathResource(path+name));
                beanFactory.registerSingleton(name.split("\\.")[0],redisScript);
            }
        }
    }
}
