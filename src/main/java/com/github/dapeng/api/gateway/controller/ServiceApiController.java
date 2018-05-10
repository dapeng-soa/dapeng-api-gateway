package com.github.dapeng.api.gateway.controller;

import com.github.dapeng.api.gateway.util.TokenUtil;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.openapi.cache.ServiceCache;
import com.github.dapeng.openapi.utils.PostUtil;
import com.today.api.admin.AdminServiceClient;
import com.today.api.admin.OpenAdminServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author struy
 */
@RestController
@RequestMapping("api")
public class ServiceApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ServiceApiController.class);
    private final OpenAdminServiceClient adminService = new OpenAdminServiceClient();

    @PostMapping
    public String rest(@RequestParam(value = "serviceName") String serviceName,
                       @RequestParam(value = "version") String version,
                       @RequestParam(value = "methodName") String methodName,
                       @RequestParam(value = "parameter") String parameter,
                       @RequestParam(value = "token") String token,
                       HttpServletRequest req) {
        if (TokenUtil.checkToken(token,adminService)) {
            LOGGER.debug("api url request :{}:{}:{}:{}", serviceName, version, methodName, parameter);
            return PostUtil.post(serviceName, version, methodName, parameter, req);
        } else {
            return "Check permissions";
        }
    }

    @PostMapping(value = "/{serviceName}/{version}/{methodName}")
    public String rest1(@PathVariable(value = "serviceName") String serviceName,
                        @PathVariable(value = "version") String version,
                        @PathVariable(value = "methodName") String methodName,
                        @RequestParam(value = "parameter") String parameter,
                        @RequestParam(value = "token") String token,
                        HttpServletRequest req) {
        if (TokenUtil.checkToken(token,adminService)) {
            LOGGER.debug("api url request :{}:{}:{}:{}", serviceName, version, methodName, parameter);
            return PostUtil.post(serviceName, version, methodName, parameter, req);
        } else {
            return "Check permissions";
        }
    }

    @GetMapping(value = "/list")
    public ResponseEntity<?> list() {
        Map<String, Service> services = ServiceCache.getServices();
        List<String> list = new ArrayList(16);
        services.forEach((k, v) -> {
            list.add(v.namespace + "." + v.name + ":" + v.meta.version);
        });
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }
}
