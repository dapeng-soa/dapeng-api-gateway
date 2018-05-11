package com.github.dapeng.api.gateway.pojo;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author with struy.
 * Create by 2018/5/12 00:00
 * email :yq1724555319@gmail.com
 */
@Root(name = "service-whitelist")
public class ServiceWhitelist {
    @ElementList(entry = "service", inline = true)
    protected List<String> service;

    public List<String> getService() {
        return service;
    }
}
