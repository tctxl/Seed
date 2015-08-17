package com.opdar.framework.web.common;

import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.SeedWeb;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Jeffrey on 2015/4/20.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedPath {
    /**
     * 0.文件类型
     * 1.classpath类型
     */
    private int pathType = 0;
    private String path;
    private String mapping;
    private ClassLoader loader;

    public Map<String,String> pathMappings = new HashMap<String,String>();
    public SeedPath(String key, String value, ClassLoader loader) {
        mapping = key.toUpperCase();
        String path = Utils.getClassPath(value);
        if(path != null){
            pathType = 1;
            JarFile jar = null;
            try {
                URL url = loader.getResource(key.substring(1));
                if(url != null){
                    String protocol = url.getProtocol();
                    if(protocol.equals("jar")){
                        jar = ((JarURLConnection)loader.getResource(key.substring(1)).openConnection()).getJarFile();;
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            String publick = key.substring(1).concat("/");
                            if(name.indexOf(publick) == 0){
                                name = name.replace(publick, "");
                                pathMappings.put(name.toUpperCase(),name);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            path = value;
        }
        this.loader = loader;
        this.path = Utils.testRouter(path.replace(".","/")).substring(1);
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public int getPathType() {
        return pathType;
    }

    public void setPathType(int pathType) {
        this.pathType = pathType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public InputStream getResourceAsStream(String file) throws FileNotFoundException {
        file = Utils.testRouter(file);
        String path = this.path+file;
        if(SeedWeb.RESOURCE_MAPPING.containsKey(path.toUpperCase())){
            path = SeedWeb.RESOURCE_MAPPING.get(path.toUpperCase());
        }
        if(pathType == 0){
            File f = new File(System.getProperty("seed.root"),path);
            if(!f.exists()){
                return null;
            }
            return new FileInputStream(f);
        }else if(pathType == 1){
            if(pathMappings.containsKey(file.toUpperCase())){
                file = pathMappings.get(file.toUpperCase());
                path = this.path + file;
            }
            return loader.getResourceAsStream(path);
        }
        return null;
    }

    public File getFilePath(String file){
        file = Utils.testRouter(file);
        String path = this.path+file;
        if(SeedWeb.RESOURCE_MAPPING.containsKey(path.toUpperCase())){
            path = SeedWeb.RESOURCE_MAPPING.get(path.toUpperCase());
        }
        return new File(System.getProperty("seed.root"),path);
    }

}
