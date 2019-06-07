package com.uetty.rule.config.redis.script;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;

@Configuration
public class ScriptConfig {

    private static Map<String, DefaultRedisScript> SCRIPT_MAP = Maps.newHashMap();

    @Value("${spring.redis.luaPath}")
    private String luaPath;

    /**
     * 初始化lua脚本
     */
    @PostConstruct
    public void initScript() {
        String path = this.getClass().getResource(luaPath).getPath();
        File file = new File(path);
        if (file.isDirectory()) {
            String[] luaFiles = file.list();
            if (luaFiles!=null){
                for (String name : luaFiles) {
                    DefaultRedisScript redisScript = new DefaultRedisScript();
                    redisScript.setLocation(new ClassPathResource(luaPath + name));
                    SCRIPT_MAP.put(name.split("\\.")[0], redisScript);
                }
            }
        }
    }

    public static <T> DefaultRedisScript<T> getScript(ScriptType scriptType) {
        return SCRIPT_MAP.get(scriptType.key);
    }

    public enum ScriptType {

        HGET("hget"),
        GET_HASH_FROM_ZSET("getHashFromZset"),
        LOCK("lock"),
        UN_LOCK("unLock"),
        SCHEDULE_LOCK("scheduleLock");

        private String key;

        ScriptType(String key) {
            this.key = key;
        }
    }
}
