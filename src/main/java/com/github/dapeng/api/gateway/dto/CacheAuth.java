package com.github.dapeng.api.gateway.dto;

import java.sql.Timestamp;

/**
 * @author with struy.
 * Create by 2018/5/7 15:00
 * email :yq1724555319@gmail.com
 */

public class CacheAuth {
    private String authKey;
    private Long timeout;
    private Long cacheTime;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public static void main(String [] args){
        Long start = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new Timestamp(1525679706066L+(10 * 60 * 1000L)));
    }
}
