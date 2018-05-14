package com.github.dapeng.api.gateway;

import com.today.eventbus.rest.support.RestListenerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述: com.github.dapeng.api.gateway
 *
 * @author hz.lei
 * @date 2018年05月03日 上午9:42
 */
@Configuration
public class BeanConfiguration {


    @Bean
    public RestListenerFactory restListenerFactory() {
        return new RestListenerFactory();
    }


}
