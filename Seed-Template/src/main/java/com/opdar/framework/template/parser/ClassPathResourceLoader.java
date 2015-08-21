package com.opdar.framework.template.parser;

import com.opdar.framework.template.res.Loader;
import com.opdar.framework.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class ClassPathResourceLoader extends Loader {

    public ClassLoader loader = Thread.currentThread().getContextClassLoader();
    public ClassPathResourceLoader(){}
    public ClassPathResourceLoader(ClassLoader loader){
        this.loader = loader;
    }

    public String parse(String path,Object dataModel){
        try {
            Resolver resolver = new Resolver(new String(Utils.is2byte(load(path)),super.charsetName),this);
            resolver.setPath(path);
            return resolver.parse(dataModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream load(String path) {
        return loader.getResourceAsStream(basePath+path);
    }

}
