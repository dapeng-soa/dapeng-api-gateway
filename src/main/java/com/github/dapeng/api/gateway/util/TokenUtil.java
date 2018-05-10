package com.github.dapeng.api.gateway.util;

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
    private static final String NOTFIND_CODE = "000000002";
    private static final String TOKEN_SPLIT_KEY = "@##@";
    private static final Long TIMEOUT = 60000L;


    /**
     * 检查token是否合法
     * @param token
     * @param adminService
     * @return
     */
    public static Boolean checkToken(String token, OpenAdminServiceClient adminService) {

        try {
            if (null != token) {
                // token :apikey@##@timesteamp
                String decodeToken = Base64Util.decode(token);
                if (null != decodeToken && decodeToken.contains(TOKEN_SPLIT_KEY)) {
                    String[] tokenTemp = decodeToken.split(TOKEN_SPLIT_KEY);
                    String apiKey = tokenTemp[0];
                    Long timesTeamp = Long.valueOf(tokenTemp[1]); // 毫秒值
                    boolean timeouted = (System.currentTimeMillis() - timesTeamp) > TIMEOUT;
                    // 缓存中获取对应的规则
                    String ipRules = cacheAuthMap.get(apiKey);

                    if (null == ipRules) {
                        // 通过adminService获取对应的规则
                        return chekApiKeyInfoByService(apiKey, adminService) && !timeouted;
                    }
                    return checkRule(ipRules) && !timeouted;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(TokenUtil.class.getName() + "::check Token error ,token [" + token + "]", e);
            return false;
        }
    }

    /**
     * 检查规则是否符合
     * @param ipRules
     * @return
     */
    private static Boolean checkRule(String ipRules) {
        /* 调用者真实ip */
        String invokeIp = InvokeUtil.getIpAddress();
        LOGGER.debug("gateway invoke Ip:[{"+invokeIp+"}]");
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

    /**
     * 获取apiKeyInfo
     * @param apikey
     * @param adminService
     * @return
     */
    private static Boolean chekApiKeyInfoByService(String apikey, OpenAdminServiceClient adminService) {
        try {
            TApiKeyInfo apiKeyInfo = adminService.getGateWayApiKeyInfo(apikey);
            if (!cacheAuthMap.containsKey(apiKeyInfo.getApiKey())) {
                cacheAuthMap.put(apiKeyInfo.getApiKey(), apiKeyInfo.getIpRule());
            }
            return checkRule(apiKeyInfo.getIpRule());
        } catch (SoaException e) {
            if (NOTFIND_CODE.equals(e.getCode())) {
                LOGGER.info(e.getMsg(), e);
            } else {
                LOGGER.error(TokenUtil.class.getName() + "::find apiKey info error [" + apikey + "]", e);
            }
            return false;
        }
    }
}
