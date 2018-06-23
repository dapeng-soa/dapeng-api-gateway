package com.github.dapeng.api.gateway.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author with struy.
 * Create by 2018/5/9 15:10
 * email :yq1724555319@gmail.com
 */

public class InvokeUtil {
    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {

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
        return ip;
    }

    public static HttpServletRequest getHttpRequest() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }

        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    public static Map<String, String> getCookies() {
        HttpServletRequest request = getHttpRequest();
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
