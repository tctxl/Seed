package com.opdar.framework.utils;


import com.opdar.framework.asm.Type;

import java.lang.reflect.Field;
/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class FieldModel {
    private Field field;
    private Type type;
    private Object value;
    private String name;

    @Override
	public String toString() {
		return "FieldModel [field=" + field + ", type=" + type + ", value="
				+ value + "]";
	}

	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}


	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
