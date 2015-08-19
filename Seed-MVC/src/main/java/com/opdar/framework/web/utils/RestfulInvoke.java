package com.opdar.framework.web.utils;

import com.opdar.framework.web.common.SeedRouter;

import java.util.HashMap;

/**
 * Created by 俊帆 on 2015/8/17.
 */
public class RestfulInvoke {
    private SeedRouter router;
    private String routerName;
    private HashMap<String, String> restfulResult;

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRestfulResult(HashMap<String, String> restfulResult) {
        this.restfulResult = restfulResult;
    }

    public HashMap<String, String> getRestfulResult() {
        return restfulResult;
    }

    public void setRouter(SeedRouter router) {
        this.router = router;
    }

    public SeedRouter getRouter() {
        return router;
    }
}
