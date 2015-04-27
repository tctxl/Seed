package com.opdar.framework.configureweb.beans;

import com.opdar.framework.db.anotations.Field;
import com.opdar.framework.db.anotations.Table;

/**
 * Created by Jeffrey on 2015/4/23.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ConfigureEntity {
    @Field("_id")
    private String id;
    private String configureName;
    private String configureValue;
    private String description ;
    private Integer version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfigureName() {
        return configureName;
    }

    public void setConfigureName(String configureName) {
        this.configureName = configureName;
    }

    public String getConfigureValue() {
        return configureValue;
    }

    public void setConfigureValue(String configureValue) {
        this.configureValue = configureValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
