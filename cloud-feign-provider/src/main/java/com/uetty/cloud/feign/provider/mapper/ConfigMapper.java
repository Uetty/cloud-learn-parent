package com.uetty.cloud.feign.provider.mapper;


import com.uetty.cloud.feign.api.entity.Config;

public interface ConfigMapper {
    int insert(Config record);

    int insertSelective(Config record);
    
    Config getConfig(String name);
}