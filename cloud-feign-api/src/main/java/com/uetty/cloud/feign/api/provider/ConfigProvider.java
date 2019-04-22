package com.uetty.cloud.feign.api.provider;

import com.uetty.cloud.feign.api.entity.Config;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface ConfigProvider {

    @RequestMapping(value = "getPbConfig",
            method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    String getPbConfig(String name);

    @RequestMapping(value = "getDbConfig",
            method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    Config getDbConfig(String name);
}
