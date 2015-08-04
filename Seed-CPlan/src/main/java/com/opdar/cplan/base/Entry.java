package com.opdar.cplan.base;

import com.opdar.framework.server.base.IConfig;

/**
 * Created by 俊帆 on 2015/7/23.
 */
public interface Entry {
    //入口
    public void entry();
    public IConfig getConfig();
}
