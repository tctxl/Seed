package com.opdar.cplan.utils;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by 俊帆 on 2015/7/23.
 */
public class CPlanUtils {

    static Logger logger = Logger.getLogger(CPlanUtils.class.getName());

    private CPlanUtils() {
    }

    public static void initModule() {
        String file = new File(CPlanUtils.class.getResource("/").getFile()).getParent();
        file = new File(file, "module").getAbsolutePath();
        CPLoader.loadJarPath(file);
        logger.fine(CPLoader.packages.toString());
    }

}
