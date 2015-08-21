package com.opdar.framework.template.parser;

import com.opdar.framework.template.res.Loader;
import com.opdar.framework.utils.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class FilePathResourceLoader extends Loader {

    public FilePathResourceLoader() {
    }

    public String parse(String path, Object dataModel) {
        InputStream is = null;
        try {
            is = load(path);
            Resolver resolver = new Resolver(new String(Utils.is2byte(is), super.charsetName), this);
            return resolver.parse(dataModel);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(is != null)
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is = null;
        }
        return null;
    }

    @Override
    public InputStream load(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }


}
