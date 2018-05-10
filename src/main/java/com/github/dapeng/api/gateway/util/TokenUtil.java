package com.github.dapeng.api.gateway.util;


import com.github.dapeng.api.gateway.properties.ApiGatewayProperties;
import com.github.dapeng.core.SoaException;
import com.github.dapeng.core.helper.IPUtils;
import com.today.api.admin.OpenAdminServiceClient;
import com.today.api.admin.vo.TApiKeyInfo;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author with struy.
 * Create by 2018/5/9 15:25
 * email :yq1724555319@gmail.com
 */

public class TokenUtil {

    private static final Logger LOGGER = Logger.getLogger(TokenUtil.class);
    private static Map<String, String> cacheAuthMap = new ConcurrentHashMap<>(16);
    private static final String MULTIPLEIP_CUT_KEY = ",";
    private static final String MASK_CUT_KEY = "/";
    private static final String ANY_RULE_KEY = "*";
    private static final Long TIMEOUT = 60000L;
    private static final String NOTFIND_CODE = "000000002";
    // TODO 测试
    private static final String DEFAULT_KEY = "test";
    private static String apikey;
    private static OpenAdminServiceClient adminService;

    public static Boolean checkToken(String token, OpenAdminServiceClient adminService) {
        // TODO 测试
        if (!cacheAuthMap.containsKey(DEFAULT_KEY)) {
            cacheAuthMap.put(DEFAULT_KEY, ANY_RULE_KEY);
        }

        if (null != token) {
            // token :apikey@##@timesteamp
            String decodeToken = Base64Util.decode(token);
            if (null != decodeToken && decodeToken.contains(ApiGatewayProperties.TOKEN_SPLIT_KEY)) {
                String[] tokentemp = decodeToken.split(ApiGatewayProperties.TOKEN_SPLIT_KEY);
                String apiKey = tokentemp[0];
                Long timesteamp = Long.valueOf(tokentemp[1]); // 毫秒值
                boolean timeouted = (System.currentTimeMillis() - timesteamp) > TIMEOUT;
                // 缓存中获取对应的规则
                String ipRules = cacheAuthMap.get(apiKey);

                if (null == ipRules) {
                    // 通过adminService获取对应的规则
                    return chekApiKeyInfoByService(apiKey, adminService);
                }
                return checkRule(ipRules) && timeouted;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static Boolean checkRule(String ipRules) {
        /* 调用者真实ip */
        String invokeIp = InvokeUtil.getIpAddress();
        // 如果多个IP是否命中
        if (ipRules.contains(MULTIPLEIP_CUT_KEY)) {
            return ipRules.contains(invokeIp);
        } else if (ipRules.contains(MASK_CUT_KEY)) {
            // 掩码ip查看是否命中
            String[] ips = ipRules.split(MASK_CUT_KEY);
            return IPUtils.matchIpWithMask(
                    IPUtils.transferIp(invokeIp),
                    IPUtils.transferIp(ips[0]),
                    Integer.valueOf(ips[1]));

        } else {
            // 对ip不做限制,或者单个IP值是否命中
            return ipRules.equals(ANY_RULE_KEY) || ipRules.equals(invokeIp);
        }
    }

    private static Boolean chekApiKeyInfoByService(String apikey, OpenAdminServiceClient adminService) {
        TokenUtil.apikey = apikey;
        TokenUtil.adminService = adminService;
        try {
            TApiKeyInfo apiKeyInfo = adminService.getGateWayApiKeyInfo(apikey);
            if (!cacheAuthMap.containsKey(apiKeyInfo.getApiKey())) {
                cacheAuthMap.put(apiKeyInfo.getApiKey(), apiKeyInfo.getIpRule());
            }
            return checkRule(apiKeyInfo.getIpRule());
        } catch (SoaException e) {
            if (NOTFIND_CODE.equals(e.getCode())) {
                LOGGER.info(TokenUtil.class.getName() + "::not find apiKey Info [" + apikey + "]", e);
            } else {
                LOGGER.error(TokenUtil.class.getName() + "::find apiKey info error [" + apikey + "]", e);
            }
            return false;
        }
    }
}
