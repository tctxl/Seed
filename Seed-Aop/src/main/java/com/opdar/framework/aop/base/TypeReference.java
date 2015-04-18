package com.opdar.framework.aop.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Jeffrey on 2015/4/12.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */

public abstract class TypeReference<T>
{
    public Type _type;
    protected TypeReference()
    {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() { return _type; }

}

