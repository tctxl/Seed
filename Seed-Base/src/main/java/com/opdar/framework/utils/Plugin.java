package com.opdar.framework.utils;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public interface Plugin {
    boolean install() throws Exception;
    boolean uninstall();
}
