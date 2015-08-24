package com.opdar.seed.extra.utils;

/**
 * Created by 俊帆 on 2015/7/26.
 */

import com.opdar.framework.utils.yeson.JSONObject;
import com.opdar.framework.utils.yeson.YesonParser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Created by 俊帆 on 2015/7/23.
 */
public class DecoderLoader extends URLClassLoader {

    private static final Logger logger = Logger.getLogger(DecoderLoader.class.getName());

    public final static HashMap<String, JSONObject> packages = new HashMap<String, JSONObject>();
    public final static HashMap<String, DecoderLoader> loaders = new HashMap<String, DecoderLoader>();

    public static DecoderLoader getLoader(String module) {
        return loaders.get(module);
    }

    public static Class getDecoder(String module) {
        if(packages.containsKey(module)){
            JSONObject object = packages.get(module);
            try {
                return getLoader(module).loadClass(object.get("main").toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * URLClassLoader的addURL方法
     */

    public DecoderLoader() {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
    }

    public Class defineCls(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

    /**
     * 循环遍历目录，找出所有的JAR包
     */
    private static void loopFiles(File file, List<File> files) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp, files);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                files.add(file);
            }
        }
    }

    public Class findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    /**
     * <pre>
     * 加载JAR文件
     * </pre>
     *
     * @param file
     */
    public void loadJarFile(File file) {
        try {
            addURL(file.toURI().toURL());
            JarFile jarFile = new JarFile(file);
            JarEntry entry = jarFile.getJarEntry("package.json");
            InputStream is = jarFile.getInputStream(entry);
            // 开始读取文件内容
            if (is != null) {
                String packageJSON = new String(NetUtils.read(is), "utf-8");
                YesonParser parser = new YesonParser();
                JSONObject object = parser.parse(packageJSON);
                packages.put(getModuleName(object), object);
                loaders.put(getModuleName(object), this);
            }
        } catch (Exception e) {
            logger.fine("Load jar file [".concat(file.getAbsolutePath()).concat("] failure!"));
        }
    }

    /**
     * 获取模块名称
     *
     * @param object
     * @return
     */
    private String getModuleName(JSONObject object) {
        return object.get("name").toString();
    }

    /**
     * <pre>
     * 从一个目录加载所有JAR文件
     * </pre>
     *
     * @param path
     */
    public static void loadJarPath(String path) {
        List<File> files = new ArrayList<File>();
        File lib = new File(path);
        loopFiles(lib, files);
        for (File file : files) {
            DecoderLoader cpLoader = new DecoderLoader();
            cpLoader.loadJarFile(file);
        }
    }
}
