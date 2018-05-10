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
    private static final String MULTIPLEIP_CUT_KEY = ",";
    private static final String MASK_CUT_KEY = "/";
    private static final String ANY_RULE_KEY = "*";
    private static final Long TIMEOUT = 60000L;
    // TODO 测试
    private static final String DEFAULT_KEY = "test";
    private static final String DEFAULT_IPS = "*";

    public static Boolean checkToken(String token){
        // TODO 测试
        if(cacheAuthMap.get(DEFAULT_KEY) == null){
            cacheAuthMap.put(DEFAULT_KEY,DEFAULT_IPS);
        }
        /* 调用者真实ip */
        String invokeIp = InvokeUtil.getIpAddress();

        if (null != token) {
            // token :apikey@##@timesteamp
            String decodeToken = Base64Util.decode(token);
            if (null != decodeToken && decodeToken.contains(ApiGatewayProperties.TOKEN_SPLIT_KEY)){
                String[] tokentemp = decodeToken.split(ApiGatewayProperties.TOKEN_SPLIT_KEY);
                String apiKey = tokentemp[0];
                Long timesteamp = Long.valueOf(tokentemp[1]); // 毫秒值
                boolean timeouted = (System.currentTimeMillis() - timesteamp) > TIMEOUT ;
                // 缓存中获取对应的规则
                String ipAddress =  cacheAuthMap.get(apiKey);

                if(null == ipAddress){
                    // 通过adminService获取对应的规则
                    ipAddress = "127.0.0.1";
                }
                // 如果多个IP是否命中
                if (ipAddress.contains(MULTIPLEIP_CUT_KEY)){
                    return ipAddress.contains(invokeIp) && !timeouted;
                }else if (ipAddress.contains(MASK_CUT_KEY)){
                    // 掩码ip查看是否命中
                    String[] ips = ipAddress.split(MASK_CUT_KEY);
                    return IPUtils.matchIpWithMask(
                            IPUtils.transferIp(invokeIp),
                            IPUtils.transferIp(ips[0]),
                            Integer.valueOf(ips[1])) && !timeouted;

                }else if (ipAddress.equals(ANY_RULE_KEY)){
                    // 对ip不做限制
                    return !timeouted;
                }else {
                    // 单个IP值是否命中
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
