package com.github.dapeng.api.gateway.controller;

import com.github.dapeng.api.gateway.util.InvokeUtil;
import com.github.dapeng.api.gateway.util.WhiteListUtil;
import com.github.dapeng.core.SoaException;
import com.github.dapeng.core.helper.IPUtils;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.openapi.cache.ServiceCache;
import com.github.dapeng.openapi.utils.PostUtil;
import com.today.api.admin.OpenAdminServiceClient;
import com.today.api.admin.request.CheckGateWayAuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author struy
 */
@RestController
@RequestMapping("api")
public class ServiceApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ServiceApiController.class);
    private final OpenAdminServiceClient adminService = new OpenAdminServiceClient();

    @Deprecated
    @PostMapping
    public String rest(@RequestParam(value = "serviceName") String serviceName,
                       @RequestParam(value = "version") String version,
                       @RequestParam(value = "methodName") String methodName,
                       @RequestParam(value = "parameter") String parameter,
                       HttpServletRequest req) {
        return PostUtil.post(serviceName, version, methodName, parameter, req);
    }

    @Deprecated
    @PostMapping(value = "/{serviceName}/{version}/{methodName}")
    public String rest1(@PathVariable(value = "serviceName") String serviceName,
                        @PathVariable(value = "version") String version,
                        @PathVariable(value = "methodName") String methodName,
                        @RequestParam(value = "parameter") String parameter,
                        HttpServletRequest req) {
        return PostUtil.post(serviceName, version, methodName, parameter, req);
    }

    @PostMapping(value = "/{apiKey}")
    public String authRest(@PathVariable(value = "apiKey") String apiKey,
                           @RequestParam(value = "serviceName") String serviceName,
                           @RequestParam(value = "version") String version,
                           @RequestParam(value = "methodName") String methodName,
                           @RequestParam(value = "parameter") String parameter,
                           @RequestParam(value = "timestamp") String timestamp,
                           @RequestParam(value = "secret") String secret,
                           HttpServletRequest req) {
        return proccessRequest(serviceName,
                version, methodName, apiKey, parameter, timestamp, secret, req);
    }

    @PostMapping(value = "/{serviceName}/{version}/{methodName}/{apiKey}")
    public String authRest1(@PathVariable(value = "serviceName") String serviceName,
                            @PathVariable(value = "version") String version,
                            @PathVariable(value = "methodName") String methodName,
                            @PathVariable(value = "apiKey") String apiKey,
                            @RequestParam(value = "parameter") String parameter,
                            @RequestParam(value = "timestamp") String timestamp,
                            @RequestParam(value = "secret") String secret,
                            HttpServletRequest req) {
        return proccessRequest(serviceName,
                version, methodName, apiKey, parameter, timestamp, secret, req);

    }

    /**
     * 服务列表
     *
     * @return
     */
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

    /**
     * 同步时间
     *
     * @return
     */
    @GetMapping(value = "/sysTime")
    public ResponseEntity<?> sysTime() {
        long now = System.currentTimeMillis();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(now);
    }

    /**
     * 处理请求
     *
     * @param serviceName
     * @param version
     * @param methodName
     * @param apiKey
     * @param parameter
     * @param timestamp
     * @param secret
     * @param req
     * @return
     */
    private String proccessRequest(String serviceName,
                                   String version,
                                   String methodName,
                                   String apiKey,
                                   String parameter,
                                   String timestamp,
                                   String secret,
                                   HttpServletRequest req) {
        try {
            checkSecret(serviceName, apiKey, secret, timestamp);
            return PostUtil.post(serviceName, version, methodName, parameter, req);
        } catch (SoaException e) {
            HttpServletRequest request1 = InvokeUtil.getHttpRequest();
            LOGGER.error("request failed:: Invoke ip [ {} ] apiKey:[ {} ] call timestamp:[{}] call[ {}:{}:{} ] cookies:[{}] -> ", null != request1 ? InvokeUtil.getIpAddress(request1) : IPUtils.localIp(), apiKey, timestamp, serviceName, version, methodName, InvokeUtil.getCookies(), e);
            return String.format("{\"responseCode\":\"%s\", \"responseMsg\":\"%s\", \"success\":\"%s\", \"status\":0}", e.getCode(), e.getMsg(), "{}");
        }
    }


    private void checkSecret(String serviceName, String apiKey, String secret, String timestamp) throws SoaException {
        Set<String> list = WhiteListUtil.getServiceWhiteList();
        if (null == list || !list.contains(serviceName)) {
            throw new SoaException("Err-GateWay-005", "非法请求,请联系管理员!");
        }
        HttpServletRequest request = InvokeUtil.getHttpRequest();
        String ip = request == null ? IPUtils.localIp() : InvokeUtil.getIpAddress(request);
        CheckGateWayAuthRequest checkGateWayAuthRequest = new CheckGateWayAuthRequest();
        checkGateWayAuthRequest.setApiKey(apiKey);
        checkGateWayAuthRequest.setSecret(secret);
        checkGateWayAuthRequest.setTimestamp(timestamp);
        checkGateWayAuthRequest.setInvokeIp(ip);
        adminService.checkGateWayAuth(checkGateWayAuthRequest);
    }
}
