package com.opdar.framework.web.controllers;

import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.views.FileView;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by 俊帆 on 2015/7/28.
 */

@Controller(value = "/public/")
public class ResourcesController {

    private static final String PUBLIC_DIR = "public/";

    public static Map<String,String> pathMappings = new HashMap<String,String>();
    static{
        JarFile jar = null;
        try {
            String protocol = ResourcesController.class.getClassLoader().getResource("public").getProtocol();
            if(protocol.equals("jar")){
                jar = ((JarURLConnection)ResourcesController.class.getClassLoader().getResource("public").openConnection()).getJarFile();;
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if(name.indexOf(PUBLIC_DIR) == 0){
                        name = name.replace(PUBLIC_DIR, "");
                        pathMappings.put(name.toUpperCase(),name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Router("#{res}")
    public FileView res(String res){
        if(pathMappings.containsKey(res))res =  pathMappings.get(res);
        String contentType = new MimetypesFileTypeMap().getContentType(res.toLowerCase());
        return new FileView(getClass().getClassLoader().getResourceAsStream(PUBLIC_DIR+res),contentType,null);
    }
}
