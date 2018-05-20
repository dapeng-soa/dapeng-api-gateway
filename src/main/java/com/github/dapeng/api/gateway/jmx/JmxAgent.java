package com.github.dapeng.api.gateway.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * @author with struy.
 * Create by 2018/5/18 11:00
 * email :yq1724555319@gmail.com
 */

public class JmxAgent {
    private final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    private static Logger LOGGER = LoggerFactory.getLogger(JmxAgent.class);

    public void registerMbean() {
        try {
            //create mbean and register mbean
            ObjectName mName = new ObjectName("com.github.dapeng:name=gatewayInfo");
            server.registerMBean(new GateWayInfo(), mName);
            LOGGER.info("::registerMBean GateWayInfo success");
        } catch (Exception e) {
            LOGGER.info("::registerMBean GateWayInfo error", e);
        }
    }
}
