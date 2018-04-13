package com.github.dapeng.api.gateway.controller;

import com.github.dapeng.openapi.utils.PostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author struy
 */
@RestController
@RequestMapping("api")
public class ServiceApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ServiceApiController.class);

    @PostMapping
    public String rest(@RequestParam(value = "serviceName") String serviceName,
                       @RequestParam(value = "version") String version,
                       @RequestParam(value = "methodName") String methodName,
                       @RequestParam(value = "parameter") String parameter,
                       HttpServletRequest req) {
        LOGGER.debug("api request :{}:{}:{}:{}", serviceName, version, methodName, parameter);
        return PostUtil.post(serviceName, version, methodName, parameter, req);
    }

}
