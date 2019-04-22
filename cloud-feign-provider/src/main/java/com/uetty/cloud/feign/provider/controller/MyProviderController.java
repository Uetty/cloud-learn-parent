package com.uetty.cloud.feign.provider.controller;

import com.uetty.cloud.feign.api.entity.Config;
import com.uetty.cloud.feign.api.provider.ConfigProvider;
import com.uetty.cloud.feign.api.provider.DecryptPassProvider;
import com.uetty.cloud.feign.provider.service.impl.ConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyProviderController implements ConfigProvider, DecryptPassProvider {

	@Value("${spring.datasource.password}")
	private String password;

	@Autowired
	private ConfigServiceImpl configService;

	@Override
	public String getPbConfig(String name) {
		return configService.getPbConfig(name);
	}

	@Override
	public Config getDbConfig(String name) {
		return configService.getDbConfig(name);
    }


	@Override
	public String getDecryptedPass(String name) {
		return "password => " + password;
	}
}
