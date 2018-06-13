package com.github.dapeng.api.gateway;

import com.github.dapeng.api.gateway.util.InvokeUtil;
import com.github.dapeng.core.InvocationContext;
import com.github.dapeng.core.InvocationContextImpl;
import com.github.dapeng.core.helper.DapengUtil;
import com.github.dapeng.core.helper.IPUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ever
 */
@Service
public class SoaInvocationProxy implements InvocationContext.InvocationContextProxy {

    @PostConstruct
    public void init() {
        InvocationContextImpl.Factory.setInvocationContextProxy(this);
    }

    @Override
    public Optional<Long> sessionTid() {
        return Optional.of(DapengUtil.generateTid());
    }

    @Override
    public Optional<Integer> userIp() {
        HttpServletRequest request = InvokeUtil.getHttpRequest();

        if (request == null) {
            return Optional.of(IPUtils.localIpAsInt());
        }

        String ip = InvokeUtil.getIpAddress(request);

        return Optional.ofNullable(IPUtils.transferIp(ip));
    }

    @Override
    public Optional<Long> userId() {
        return Optional.empty();
    }

    @Override
    public Optional<Long> operatorId() {
        return Optional.empty();
    }

    @Override
    public Optional<String> callerMid() {
        HttpServletRequest request = InvokeUtil.getHttpRequest();

        if (request == null) {
            return Optional.of("apiGateWay");
        }

        return Optional.ofNullable(request.getRequestURI());
    }

    @Override
    public Map<String, String> cookies() {
        HttpServletRequest request = InvokeUtil.getHttpRequest();
        Map<String, String> cookies = new HashMap<>(16);
        if (request == null) {
            return cookies;
        }
        String cookie_storeId = request.getParameter("cookie_storeId");
        if (null != cookie_storeId) {
            cookies.put("storeId", cookie_storeId);
        }
        String cookie_posId = request.getParameter("cookie_posId");
        if (null != cookie_posId) {
            cookies.put("posId", cookie_posId);
        }
        String cookie_operatorId = request.getParameter("cookie_operatorId");
        if (null != cookie_operatorId) {
            cookies.put("operatorId", cookie_operatorId);
        }
        return cookies;
    }
}
