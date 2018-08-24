package com.github.dapeng.api.gateway.controller;

import com.github.dapeng.api.gateway.util.InvokeUtil;
import com.github.dapeng.api.gateway.util.WhiteListUtil;
import com.github.dapeng.core.SoaCode;
import com.github.dapeng.core.SoaException;
import com.github.dapeng.core.helper.IPUtils;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.echo.EchoClient;
import com.github.dapeng.openapi.cache.ServiceCache;
import com.github.dapeng.openapi.utils.PostUtil;
import com.today.api.admin.OpenAdminServiceClient;
import com.today.api.admin.request.CheckGateWayAuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    public DeferredResult<String> rest(@RequestParam(value = "serviceName") String serviceName,
                                       @RequestParam(value = "version") String version,
                                       @RequestParam(value = "methodName") String methodName,
                                       @RequestParam(value = "parameter") String parameter,
                                       HttpServletRequest req) {
        LOGGER.info("begin to process request using deferredResult async");
        DeferredResult<String> deferredResult = new DeferredResult<>();

        proccessNoAuthRequest(deferredResult, serviceName, version, methodName, parameter, req);


        return deferredResult;
    }

    @Deprecated
    @PostMapping(value = "/{serviceName}/{version}/{methodName}")
    public DeferredResult<String> rest1(@PathVariable(value = "serviceName") String serviceName,
                                        @PathVariable(value = "version") String version,
                                        @PathVariable(value = "methodName") String methodName,
                                        @RequestParam(value = "parameter") String parameter,
                                        HttpServletRequest req) {
        LOGGER.info("begin to process request using deferredResult async");
        DeferredResult<String> deferredResult = new DeferredResult<>();
        proccessNoAuthRequest(deferredResult, serviceName, version, methodName, parameter, req);
        return deferredResult;
    }

    @PostMapping(value = "/{apiKey}")
    public DeferredResult<String> authRest(@PathVariable(value = "apiKey") String apiKey,
                                           @RequestParam(value = "serviceName") String serviceName,
                                           @RequestParam(value = "version") String version,
                                           @RequestParam(value = "methodName") String methodName,
                                           @RequestParam(value = "parameter") String parameter,
                                           @RequestParam(value = "timestamp") String timestamp,
                                           @RequestParam(value = "secret", required = false) String secret,
                                           @RequestParam(value = "secret2", required = false) String secret2,
                                           HttpServletRequest req) {
        LOGGER.info("begin to process request using deferredResult async");
        DeferredResult<String> deferredResult = new DeferredResult<>();
        proccessRequest(deferredResult, serviceName,
                version, methodName, apiKey, parameter, timestamp, secret, secret2, req);

        return deferredResult;
    }

    @PostMapping(value = "/{serviceName}/{version}/{methodName}/{apiKey}")
    public DeferredResult<String> authRest1(@PathVariable(value = "serviceName") String serviceName,
                                            @PathVariable(value = "version") String version,
                                            @PathVariable(value = "methodName") String methodName,
                                            @PathVariable(value = "apiKey") String apiKey,
                                            @RequestParam(value = "parameter") String parameter,
                                            @RequestParam(value = "timestamp") String timestamp,
                                            @RequestParam(value = "secret", required = false) String secret,
                                            @RequestParam(value = "secret2", required = false) String secret2,
                                            HttpServletRequest req) {

        DeferredResult<String> deferredResult = new DeferredResult<>();
        proccessRequest(deferredResult, serviceName,
                version, methodName, apiKey, parameter, timestamp, secret, secret2, req);

        return deferredResult;

    }

    /**
     * 调用指定服务的echo方法，判断服务是否健康
     *
     * @param serviceName 服务名
     * @param version     服务版本号
     * @return
     */
    @GetMapping(value = "/echo/{service}/{version}")
    public String echo(@PathVariable(value = "service") String serviceName,
                       @PathVariable(value = "version") String version) throws SoaException {
        return new EchoClient(serviceName, version).echo();
    }

    /**
     * 服务列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ResponseEntity<?> list() {
        Map<String, Service> services = ServiceCache.getServices();
        List<String> list = new ArrayList<>(16);
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
    private void proccessRequest(DeferredResult<String> deferredResult,
                                 String serviceName,
                                 String version,
                                 String methodName,
                                 String apiKey,
                                 String parameter,
                                 String timestamp,
                                 String secret,
                                 String secret2,
                                 HttpServletRequest req) {
//            checkSecret(serviceName, apiKey, secret, timestamp, parameter, secret2);
        CompletableFuture<String> jsonResponse = (CompletableFuture<String>) PostUtil.postAsync(serviceName, version, methodName, parameter, req);
        jsonResponse.whenComplete((result, ex) -> {
            if (ex != null) {
                deferredResult.setResult(String.format("{\"responseCode\":\"%s\", \"responseMsg\":\"%s\", \"success\":\"%s\", \"status\":0}", SoaCode.ServerUnKnown.getCode(), ex.getMessage(), "{}"));
            } else {

                if (result.contains("status")) {
                    deferredResult.setResult(result);
                    return;
                }
                String response = "{}".equals(result) ? "{\"status\":1}" : result.substring(0, result.lastIndexOf('}')) + ",\"status\":1}";
                deferredResult.setResult(response);
            }
        });

    }


    private void proccessNoAuthRequest(DeferredResult<String> deferredResult,
                                       String serviceName,
                                       String version,
                                       String methodName,
                                       String parameter,
                                       HttpServletRequest req) {
        CompletableFuture<String> jsonResponse = (CompletableFuture<String>) PostUtil.postAsync(serviceName, version, methodName, parameter, req);
        jsonResponse.whenComplete((result, ex) -> {
            if (ex != null) {
                deferredResult.setResult(String.format("{\"responseCode\":\"%s\", \"responseMsg\":\"%s\", \"success\":\"%s\", \"status\":0}", SoaCode.ServerUnKnown.getCode(), ex.getMessage(), "{}"));
            } else {

                if (result.contains("status")) {
                    deferredResult.setResult(result);
                    return;
                }
                String response = "{}".equals(result) ? "{\"status\":1}" : result.substring(0, result.lastIndexOf('}')) + ",\"status\":1}";
                deferredResult.setResult(response);
            }
        });
    }


    private void checkSecret(String serviceName, String apiKey, String secret, String timestamp, String parameter, String secret2) throws SoaException {
        Set<String> list = WhiteListUtil.getServiceWhiteList();
        if (null == list || !list.contains(serviceName)) {
            throw new SoaException("Err-GateWay-006", "非法请求,请联系管理员!");
        }
        HttpServletRequest request = InvokeUtil.getHttpRequest();
        String ip = request == null ? IPUtils.localIp() : InvokeUtil.getIpAddress(request);
        CheckGateWayAuthRequest checkGateWayAuthRequest = new CheckGateWayAuthRequest();
        checkGateWayAuthRequest.setApiKey(apiKey);
        checkGateWayAuthRequest.setSecret(Optional.ofNullable(secret));
        checkGateWayAuthRequest.setTimestamp(timestamp);
        checkGateWayAuthRequest.setInvokeIp(ip);
        checkGateWayAuthRequest.setParameter(Optional.ofNullable(parameter));
        checkGateWayAuthRequest.setSecret2(Optional.ofNullable(secret2));
        adminService.checkGateWayAuth(checkGateWayAuthRequest);
    }
}
