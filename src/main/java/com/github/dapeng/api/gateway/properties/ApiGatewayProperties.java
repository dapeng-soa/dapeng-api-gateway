package com.github.dapeng.api.gateway.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author maple.lei
 */
@ConfigurationProperties(prefix = "soa.zookeeper")
public class ApiGatewayProperties {
    private static Logger logger = LoggerFactory.getLogger(ApiGatewayProperties.class);

    public static final String ENV_SOA_ZOOKEEPER_HOST = "soa_zookeeper_host";

    public static final String PROP_SOA_ZOOKEEPER_HOST = "soa.zookeeper.host";

    public static final String AUTH_CACHE_KEY = "auth_key";

    public static final Long AUTH_CACHE_TIMEOUT = 10 * 60 * 1000L;

    private String host;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}


