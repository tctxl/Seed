package com.opdar.framework.web.controllers;

import com.opdar.framework.utils.ResourceUtils;
import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.views.FileView;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
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

    private boolean isClassPath = false;
    public Map<String, String> pathMappings = new HashMap<String, String>();
    File file;

    public ResourcesController() {
        if (isClassPath) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                URL resource = loader.getResource("public");
                String protocol = resource.getProtocol();
                if (protocol.equals("jar")) {
                    JarFile jar = ((JarURLConnection) loader.getResource("public").openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.indexOf("public/") == 0) {
                            name = name.replace("public/", "");
                            pathMappings.put(name.toUpperCase(), name);
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            URL url = Thread.currentThread().getContextClassLoader().getResource("/");
            File webinf = new File(url.getPath()).getParentFile().getParentFile();
            file = new File(webinf, "pathValue");
            pathMappings.putAll(ResourceUtils.findMapping("", file.getPath()));
        }
    }

    @Router("#{res}")
    public Object res(String res) {
        String contentType = new MimetypesFileTypeMap().getContentType(res.toLowerCase());
        if (isClassPath) {
            if (pathMappings.containsKey(res)) res = pathMappings.get(res);
            return new FileView(Thread.currentThread().getContextClassLoader().getResourceAsStream("public/" + res), contentType, null);
        } else {
            if (pathMappings.containsKey(res)) res = pathMappings.get(res);
            File file_ = new File(file, res);
            return new FileView(file_, contentType, null);
        }
    }
}
