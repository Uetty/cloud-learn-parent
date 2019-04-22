package com.uetty.cloud.feign.api.provider;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface DecryptPassProvider {

    @RequestMapping(value = "getDecryptedPass",
            method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    String getDecryptedPass(String name);
}
