package com.opdar.cplan.utils;

import com.opdar.framework.utils.yeson.JSONObject;
import com.opdar.framework.utils.yeson.YesonParser;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by 俊帆 on 2015/7/23.
 */
public class CPlanUtils {

    private Method addURL = initAddMethod();
    private static URLClassLoader system = (URLClassLoader) Thread.currentThread().getContextClassLoader();

    /** 初始化方法 */
    private Method initAddMethod() {
        try {
            Method add = URLClassLoader.class
                    .getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String,JSONObject> packages = new HashMap<String, JSONObject>();

    public static void initModule(){
        CPlanUtils cPlanUtils = new CPlanUtils();
        String file =new File(CPlanUtils.class.getResource("/").getFile()).getParent();
        file = new File(file,"module").getAbsolutePath();
        cPlanUtils.loadJarPath(file);
        System.out.println(packages);

    }

    public static void main(String[] args) {
        initModule();
        System.out.println(new File(CPlanUtils.class.getResource("/").getFile()));
    }

    /**
     * 循环遍历目录，找出所有的JAR包
     */
    private void loopFiles(File file, List<File> files) {
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

    /**
     * <pre>
     * 加载JAR文件
     * </pre>
     *
     * @param file
     */
    public void loadJarFile(File file) {
        try {
            addURL.invoke(system, new Object[]{file.toURI().toURL()});
            System.out.println("Loaded Jar：" + file.getAbsolutePath());
            JarFile jarFile = new JarFile(file);
            JarEntry entry = jarFile.getJarEntry("package.json");
            InputStream is = jarFile.getInputStream(entry);
            // 开始读取文件内容
            if (is != null) {
                String packageJSON = new String(Utils.read(is), "utf-8");
                YesonParser parser = new YesonParser();
                JSONObject object = parser.parse(packageJSON);
                packages.put(object.get("module-name").toString(),object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 从一个目录加载所有JAR文件
     * </pre>
     *
     * @param path
     */
    public void loadJarPath(String path) {
        List<File> files = new ArrayList<File>();
        File lib = new File(path);
        loopFiles(lib, files);
        for (File file : files) {
            loadJarFile(file);
        }
    }
}
