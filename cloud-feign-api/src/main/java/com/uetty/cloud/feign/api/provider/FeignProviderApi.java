package com.uetty.cloud.feign.api.provider;

import com.uetty.cloud.feign.api.entity.Config;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface FeignProviderApi {

    @RequestMapping(value = "getPbConfig", method = RequestMethod.GET)
    String getPbConfig(@RequestParam String name);

    /* post 的时候要加上head为json，不然收不到参数 */
    @RequestMapping(value = "getDbConfig", method = RequestMethod.POST,
            headers = {"Content-Type=application/json"})
    Config getDbConfig(@RequestParam String name);

    @RequestMapping(value = "getDecryptedPass")
    String getDecryptedPass();

}
