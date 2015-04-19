package com.opdar.framework.server.exceptions;

/**
 * Created by jeffrey on 2015/4/19.
 */
public class NoConfigException extends Throwable {
    public NoConfigException(){
        super("Please implement IConfig.class");
    }
}
