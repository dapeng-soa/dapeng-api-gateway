package com.github.dapeng.api.gateway.dto;

/**
 * @author with struy.
 * Create by 2018/5/7 00:06
 * email :yq1724555319@gmail.com
 */

public class ApiKeyInfo {
    /**
     * 鉴权key
     */
    private String apiKey;
    /**
     * 描述
     */
    private String label;

    /**
     * ip规则
     */
    private String ipAddress;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
