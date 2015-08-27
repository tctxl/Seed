package com.opdar.framework.server.supports;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.utils.CloseCallback;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public abstract class DefaultSupport implements ISupport {
    public static IConfig config = null;
    public ISupport config(IConfig config) {
        this.config = config;
        return this;
    }
}
