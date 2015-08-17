package com.opdar.framework.template.res;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public abstract class Loader {
    protected String currentPath = "";
    protected String charsetName = "utf-8";
    public abstract InputStream load(String path) throws FileNotFoundException;

    public String getCurrentPath() {
        return currentPath;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}
