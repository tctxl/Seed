package com.opdar.framework.configureweb.controller;

/**
 * Created by Jeffrey on 2015/4/24.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ControllerInterceptor {
    public synchronized void after(){
        System.out.println("controller after");
    }

    public synchronized void before(){
        System.out.println("controller before");
    }
}
