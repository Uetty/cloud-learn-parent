package com.uetty.cloud.feign.facade.controller;

import com.uetty.cloud.feign.api.entity.Config;
import com.uetty.cloud.feign.facade.api.FeignProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FacadeController {

    @Autowired
    private FeignProviderService feignProviderService;

    @RequestMapping(value = "getPbConfig",
            method = { RequestMethod.GET, RequestMethod.POST })
    String getPbConfig(String name) {
        return feignProviderService.getPbConfig(name);
    }

    @RequestMapping(value = "getDbConfig",
            method = { RequestMethod.GET, RequestMethod.POST })
    Config getDbConfig(String name) {
        return feignProviderService.getDbConfig(name);
    }

    @RequestMapping(value = "getDecryptedPass",
            method = { RequestMethod.GET, RequestMethod.POST })
    String getDecryptedPass() {
        return feignProviderService.getDecryptedPass();
    }

}
