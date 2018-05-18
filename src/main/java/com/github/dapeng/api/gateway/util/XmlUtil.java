package com.github.dapeng.api.gateway.util;

import com.github.dapeng.api.gateway.pojo.ServiceWhitelist;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author with struy.
 * Create by 2018/5/12 00:07
 * email :yq1724555319@gmail.com
 */

public class XmlUtil {
    private static List<String> whitelist = null;
    private static Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);
    private static Persister persister = null;

    public static List<String> getServiceWhiteList() {
        if (null == whitelist) {
            return loadWhiteList();
        } else {
            return whitelist;
        }
    }

    public static List<String> loadWhiteList() {
        if (persister == null) {
            persister = new Persister();
        }
        FileInputStream inputStream = null;
        try {
            //==images==//
            inputStream = new FileInputStream("/gateway-conf/service-whitelist.xml");
            whitelist = persister.read(
                    ServiceWhitelist.class, inputStream)
                    .getService();
            LOGGER.info("load service-whitelist.xml on [/gateway-conf] current whitelist [{}]",whitelist.size());
            return whitelist;
        } catch (FileNotFoundException e) {
            LOGGER.warn("read file system NotFound [/gateway-conf/service-whitelist.xml],found conf file [service-whitelist.xml] on classpath");
            try {
                //==develop==//
                whitelist = persister.read(
                        ServiceWhitelist.class,
                        ResourceUtils.getFile("classpath:service-whitelist.xml"))
                        .getService();
                LOGGER.info("load service-whitelist.xml on [classpath] current whitelist [{}]",whitelist.size());
                return whitelist;
            } catch (FileNotFoundException e1) {
                LOGGER.error("service-whitelist.xml in [classpath] and [/gateway-conf/] NotFound, please Settings", e);
                throw new RuntimeException("service-whitelist.xml in [classpath] and [/gateway-conf/] NotFound, please Settings");
            } catch (Exception e1) {
                LOGGER.error("获取服务白名单错误!", e);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("获取服务白名单错误!", e);
            return null;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }
}
