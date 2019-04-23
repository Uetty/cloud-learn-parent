package com.uetty.cloud.feign.provider.controller;

import com.uetty.cloud.feign.api.entity.Config;
import com.uetty.cloud.feign.api.provider.FeignProviderApi;
import com.uetty.cloud.feign.provider.service.impl.ConfigServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyProviderController implements FeignProviderApi {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${spring.datasource.password}")
	private String password;

	@Autowired
	private ConfigServiceImpl configService;

	@Override
	public String getPbConfig(String name) {
		logger.info("parameter => {}", name);
		return configService.getPbConfig(name);
	}

	@Override
	public Config getDbConfig(String name) {
		logger.info("parameter => {}", name);
		return configService.getDbConfig(name);
    }


	@Override
	public String getDecryptedPass() {
		return "password => " + password;
	}
}
