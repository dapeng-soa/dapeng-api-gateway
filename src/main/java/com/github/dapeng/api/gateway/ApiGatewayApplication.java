package com.github.dapeng.api.gateway;

import com.github.dapeng.api.gateway.properties.ApiGatewayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
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
public class ApiGatewayApplication {

    @Autowired
    private GracefulShutdown gracefulShutdown;

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
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.TEXT_HTML);
        list.add(MediaType.TEXT_PLAIN);
        list.add(MediaType.APPLICATION_JSON_UTF8);
        mappingConverter.setSupportedMediaTypes(list);
        return mappingConverter;
    }

    /**
     * 优雅关机
     *
     * @return
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers(gracefulShutdown);
        return tomcat;
    }

}
