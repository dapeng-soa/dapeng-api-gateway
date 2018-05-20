package com.github.dapeng.api.gateway.jmx;

import com.github.dapeng.api.gateway.zookeeper.ZkAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author with struy.
 * Create by 2018/5/17 18:11
 * email :yq1724555319@gmail.com
 */

public class GateWayInfo implements GateWayInfoMBean {
    private static Logger LOGGER = LoggerFactory.getLogger(GateWayInfo.class);

    @Override
    public void reloadWhitelist() {
        ZkAgent.getInstance().reConnection();
        LOGGER.info("jmx try reload service whiteList");
    }
}
