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

    @PostMapping(value = "/{service}/{version}/{method}")
    public String rest(@PathVariable(value = "service") String service,
                       @PathVariable(value = "version") String version,
                       @PathVariable(value = "method") String method,
                       @RequestParam(value = "parameter") String parameter,
                       HttpServletRequest req) {
        LOGGER.debug("api request :{}:{}:{}:{}", service, version, method, parameter);
        return PostUtil.post(service, version, method, parameter, req);
    }

}
