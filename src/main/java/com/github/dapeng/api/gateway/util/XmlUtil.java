package com.github.dapeng.api.gateway.util;

import com.github.dapeng.api.gateway.pojo.ServiceWhitelist;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author with struy.
 * Create by 2018/5/12 00:07
 * email :yq1724555319@gmail.com
 */

public class XmlUtil {
    private static List<String> whitelist = null;
    private static Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

    public static List<String> getServiceWhiteList() {
        Persister persister = new Persister();
        if (null == whitelist) {
            try {
                whitelist = persister.read(
                        ServiceWhitelist.class,
                        ResourceUtils.getFile("classpath:service-whitelist.xml"))
                        .getService();
                return whitelist;
            } catch (FileNotFoundException e) {
                LOGGER.error("未在classpath下配置服务白名单文件(service-whitelist.xml),请配置", e);
                throw new RuntimeException("service-whitelist.xml NotFoundException");
            } catch (Exception e) {
                LOGGER.error("获取服务白名单错误!", e);
                return null;
            }
        } else {
            return whitelist;
        }
    }
}
