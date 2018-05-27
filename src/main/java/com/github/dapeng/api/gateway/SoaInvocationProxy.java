package com.github.dapeng.api.gateway;

import com.github.dapeng.core.InvocationContext;
import com.github.dapeng.core.InvocationContextImpl;
import com.github.dapeng.core.helper.DapengUtil;
import com.github.dapeng.core.helper.IPUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private Map<String, String> cookies = new HashMap<>(16);

    @PostConstruct
    public void init() {
        InvocationContextImpl.Factory.setInvocationContextProxy(this);
    }

    @Override
    public Optional<String> sessionTid() {
        return Optional.of(DapengUtil.generateTid());
    }

    @Override
    public Optional<String> userIp() {
        HttpServletRequest request = getHttpRequest();

        if (request == null) {
            return Optional.of(IPUtils.localIp());
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return Optional.ofNullable(ip);
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
        HttpServletRequest request = getHttpRequest();

        if (request == null) {
            return Optional.of("apiGateWay");
        }

        return Optional.ofNullable(request.getRequestURI());
    }

    @Override
    public Map<String, String> cookies() {
        return cookies;
    }

    private HttpServletRequest getHttpRequest() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }

        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
