package com.uetty.cloud.feign.provider.service.impl;

import com.uetty.cloud.feign.api.entity.Config;
import com.uetty.cloud.feign.provider.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ConfigServiceImpl {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private Environment env;

    public String getPbConfig(String name) {
        return env.getProperty(name);
    }

    public Config getDbConfig(String name) {
        return configMapper.getConfig(name);
    }
}
