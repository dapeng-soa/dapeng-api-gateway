package com.github.dapeng.api.gateway.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * desc: 系统环境变量url
 *
 * @author hz.lei
 * @since 2018年06月30日 下午4:27
 */
public class SysEnvUtil {

    private static final String KEY_HEALTH_CHECk_FLAG = "health_flag";

    public static final String HEALTH_CHECk_FLAG = get(KEY_HEALTH_CHECk_FLAG, "false");


    public static String get(String key, String defaultValue) {
        String envValue = System.getenv(key.replaceAll("\\.", "_"));

        if (envValue == null) {
            return System.getProperty(key, defaultValue);
        }

        return envValue;
    }


}
