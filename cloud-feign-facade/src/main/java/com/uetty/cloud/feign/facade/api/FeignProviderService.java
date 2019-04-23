package com.uetty.cloud.feign.facade.api;

import com.uetty.cloud.feign.api.provider.FeignProviderApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;

/**
 *  有一点坑的地方是，同名的FeignClient只能有一个，
 *  要实现多个同名的FeignClient，考虑手动实现代理：http://cloud.spring.io/spring-cloud-static/Edgware.SR2/single/spring-cloud.html#_creating_feign_clients_manually
 */
@FeignClient(name="feign-provider", configuration = FeignClientsConfiguration.class)
public interface FeignProviderService extends FeignProviderApi {
}
