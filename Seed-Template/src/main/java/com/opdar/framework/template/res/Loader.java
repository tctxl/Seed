package com.opdar.framework.template.res;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public abstract class Loader {
    protected String basePath = "";

    protected String charsetName = "utf-8";

    protected HashMap<String,Object> globalVars = new HashMap<String, Object>();

    public void setGlobalVars(String key,Object value) {
        this.globalVars.put(key,value);
    }

    public HashMap<String, Object> getGlobalVars() {
        return globalVars;
    }

    public abstract InputStream load(String path) throws FileNotFoundException;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}
