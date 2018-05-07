package com.github.dapeng.api.gateway.controller;

import com.github.dapeng.api.gateway.dto.AuthInfo;
import com.github.dapeng.api.gateway.dto.CacheAuth;
import com.github.dapeng.api.gateway.repository.GateWayRepository;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.openapi.cache.ServiceCache;
import com.github.dapeng.openapi.utils.PostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.github.dapeng.api.gateway.properties.ApiGatewayProperties;

/**
 * @author struy
 */
@RestController
@RequestMapping("api")
public class ServiceApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ServiceApiController.class);
    private static Map<String, CacheAuth> cacheAuthMap = new ConcurrentHashMap<>(1);

    @Autowired
    GateWayRepository gateWayRepository;

    @PostMapping
    public String rest(@RequestParam(value = "serviceName") String serviceName,
                       @RequestParam(value = "version") String version,
                       @RequestParam(value = "methodName") String methodName,
                       @RequestParam(value = "parameter") String parameter,
                       @RequestParam(value = "authKey") String authKey,
                       HttpServletRequest req) {
        if (checkAuthKey(authKey)) {
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
                        @RequestParam(value = "authKey") String authKey,
                        HttpServletRequest req) {
        if (checkAuthKey(authKey)) {
            LOGGER.debug("api url request :{}:{}:{}:{}", serviceName, version, methodName, parameter);
            return PostUtil.post(serviceName, version, methodName, parameter, req);
        } else {
            return "Check permissions";
        }
    }

    @GetMapping(value = "/list/{authKey}")
    public ResponseEntity<?> list(@PathVariable(value = "authKey") String authKey) {

        if (checkAuthKey(authKey)) {
            Map<String, Service> services = ServiceCache.getServices();
            List<String> list = new ArrayList(16);
            services.forEach((k, v) -> {
                list.add(v.namespace + "." + v.name + ":" + v.meta.version);
            });
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(list);
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Check permissions");
        }

    }

    private Boolean checkAuthKey(String authKey) {
        if (null != authKey) {
            CacheAuth cacheAuth = cacheAuthMap.get(ApiGatewayProperties.AUTH_CACHE_KEY);
            if (null != cacheAuth) {
                // check timeout
                if (System.currentTimeMillis() < (cacheAuth.getCacheTime() + cacheAuth.getTimeout())) {
                    return cacheAuth.getAuthKey().equals(authKey);
                } else {
                    cacheAuthMap.remove(ApiGatewayProperties.AUTH_CACHE_KEY);
                    return checkByDb(authKey);
                }
            } else {
                return checkByDb(authKey);
            }
        } else {
            return false;
        }
    }

    private Boolean checkByDb(String authKey) {
        // query db
        AuthInfo byAuthKey = gateWayRepository.findByAuthKey(authKey);
        if (null != byAuthKey) {
            CacheAuth cacheAuth1 = new CacheAuth();
            cacheAuth1.setCacheTime(System.currentTimeMillis());
            cacheAuth1.setAuthKey(byAuthKey.getAuthKey());
            cacheAuth1.setTimeout(ApiGatewayProperties.AUTH_CACHE_TIMEOUT);
            // cache
            cacheAuthMap.put(ApiGatewayProperties.AUTH_CACHE_KEY, cacheAuth1);
            return byAuthKey.getAuthKey().equals(authKey);
        } else {
            return false;
        }
    }

}
