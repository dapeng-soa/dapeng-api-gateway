package com.github.dapeng.api.gateway.util;


import com.github.dapeng.api.gateway.properties.ApiGatewayProperties;
import com.github.dapeng.core.helper.IPUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author with struy.
 * Create by 2018/5/9 15:25
 * email :yq1724555319@gmail.com
 */

public class TokenUtil {

    private static Map<String, String> cacheAuthMap = new ConcurrentHashMap<>(16);

    public static Boolean checkToken(String token) {
        if(cacheAuthMap.get("test") == null){
            cacheAuthMap.put("test","127.0.0.1");
        }
        /* 调用者真实ip */
        String invokeIp = InvokeUtil.getIpAddress();

        if (null != token) {
            // token :apikey@##@timesteamp
            String decodeToken = Base64Util.decode(token);
            if (null != decodeToken){
                String[] tokentemp = decodeToken.split(ApiGatewayProperties.TOKEN_SPLIT_KEY);
                String apiKey = tokentemp[0];
                Long timesteamp = Long.valueOf(tokentemp[1]);
                boolean timeouted = (System.currentTimeMillis() - timesteamp) > 60000;
                // 缓存中获取对应的规则
                String ipAddress =  cacheAuthMap.get(apiKey);

                if(null == ipAddress){
                    // 通过adminService获取对应的规则
                    ipAddress = "127.0.0.1";
                }
                // 如果多个ip则查看是否包含
                if (ipAddress.contains(",")){
                    return ipAddress.contains(invokeIp) && !timeouted;
                    // 掩码ip查看是否在范围里
                }else if (ipAddress.contains("/")){
                    String[] ips = ipAddress.split("/");
                    return IPUtils.matchIpWithMask(
                            IPUtils.transferIp(invokeIp),
                            IPUtils.transferIp(ips[0]),
                            Integer.valueOf(ips[1])) && !timeouted;
                // 单个IP查看是否符合
                }else {
                    return ipAddress.equals(invokeIp) && !timeouted;
                }
            }else {
                return false;
            }
        } else {
            return false;
        }
    }
}
