package com.github.dapeng.api.gateway.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author with struy.
 * Create by 2018/5/7 00:06
 * email :yq1724555319@gmail.com
 */

@Entity
@Table(name = "auth_info")
public class AuthInfo {

    @Id
    private int id;
    /**
     * 鉴权key
     */
    private String authKey;
    /**
     * 描述
     */
    private String label;
    /**
     * 创建时间
     */
    private java.sql.Timestamp createDate;
    /**
     * 更新时间
     */
    private java.sql.Timestamp updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }
}
