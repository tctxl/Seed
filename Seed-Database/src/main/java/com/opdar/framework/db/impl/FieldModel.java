package com.opdar.framework.db.impl;

import java.lang.reflect.Field;

/**
 * Created by Jeffrey on 2015/4/26.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class FieldModel {
    Class<?> type;

    String mapping;
    Field field;
    ChildSql childSql;

    public FieldModel(Class<?> type, ChildSql childSql, Field field) {
        this.type = type;
        this.childSql = childSql;
        this.field = field;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public ChildSql getChildSql() {
        return childSql;
    }

    public void setChildSql(ChildSql childSql) {
        this.childSql = childSql;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
