package com.github.dapeng.api.gateway;

import com.github.dapeng.api.gateway.jmx.JmxAgent;
import com.github.dapeng.api.gateway.properties.ApiGatewayProperties;
import com.github.dapeng.api.gateway.util.XmlUtil;
import com.github.dapeng.openapi.cache.ZkBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author struy
 */
@SpringBootApplication
@EnableConfigurationProperties(ApiGatewayProperties.class)
public class ApiGatewayApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGatewayApplication.class);

    @Autowired
    private ApiGatewayProperties properties;

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .sources(ApiGatewayApplication.class)
                .run(args);
    }

    /**
     * 针对前端ajax的消息转换器
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> list = new ArrayList();
        list.add(MediaType.TEXT_HTML);
        list.add(MediaType.TEXT_PLAIN);
        list.add(MediaType.APPLICATION_JSON_UTF8);
        mappingConverter.setSupportedMediaTypes(list);
        return mappingConverter;
    }

    @Override
    public void run(String... args) throws Exception {
        if (System.getenv(ApiGatewayProperties.ENV_SOA_ZOOKEEPER_HOST) != null
                || System.getProperty(ApiGatewayProperties.PROP_SOA_ZOOKEEPER_HOST) != null) {
            LOGGER.info("zk host in the environment is already setter...");
        } else {
            System.setProperty(ApiGatewayProperties.PROP_SOA_ZOOKEEPER_HOST, properties.getHost());
            LOGGER.info("zk host in the environment is not found,setting it with spring boot application, host is {}", properties.getHost());
        }
        new JmxAgent().registerMbean();
        XmlUtil.loadWhiteList();
        new ZkBootstrap().init();
    }


}
