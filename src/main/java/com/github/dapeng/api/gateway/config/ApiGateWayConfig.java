package com.github.dapeng.api.gateway.config;

import com.github.dapeng.api.gateway.properties.ApiGatewayProperties;
import com.github.dapeng.api.gateway.util.WhiteListUtil;
import com.github.dapeng.openapi.cache.ZkBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * desc: zk metadata fetch init
 *
 * @author hz.lei
 * @since 2018年06月21日 上午10:40
 */
@Component
public class ApiGateWayConfig implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGateWayConfig.class);

    @Autowired
    private ApiGatewayProperties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (System.getenv(ApiGatewayProperties.ENV_SOA_ZOOKEEPER_HOST) != null
                || System.getProperty(ApiGatewayProperties.PROP_SOA_ZOOKEEPER_HOST) != null) {
            LOGGER.info("zk host in the environment is already setter...");
        } else {
            System.setProperty(ApiGatewayProperties.PROP_SOA_ZOOKEEPER_HOST, properties.getHost());
            LOGGER.info("zk host in the environment is not found,setting it with spring boot application, host is {}", properties.getHost());
        }
        new ZkBootstrap().filterInitWhiteList(WhiteListUtil.initWhiteList());
    }
}
