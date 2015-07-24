package com.opdar.framework.server.base;

import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;

/**
 * Created by shiju_000 on 2015/4/8.
 */
public interface ISupport {
    ISupport start();
    ISupport config(IConfig config);
    void scanController(String path,boolean isClear,String perfix);

}
