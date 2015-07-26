package com.opdar.cplan.utils;

import java.io.File;
/**
 * Created by 俊帆 on 2015/7/23.
 */
public class CPlanUtils {
    public static void initModule() {
        String file = new File(CPlanUtils.class.getResource("/").getFile()).getParent();
        file = new File(file, "module").getAbsolutePath();
        CPLoader.loadJarPath(file);
        System.out.println(CPLoader.packages);
    }

}
