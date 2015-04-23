package com.opdar.framework.server.base;

/**
 * Created by shiju_000 on 2015/4/8.
 */
public interface IConfig {
    public static String CONTROLLER_PATH = "com.opdar.seed.controllers";
    public static String PORT = "com.opdar.seed.port";
    public static String PAGES = "com.opdar.seed.web.pages";
    public static String PUBLIC = "com.opdar.seed.web.public";
    public static String DEFAULT_PAGES = "com.opdar.seed.web.default.pages";
    public static String ACTIVE_RECORD = "com.opdar.seed.activerecord";


    public static String JDBC_URL = "com.opdar.seed.sql.jdbcUrl";
    public static String JDBC_USERNAME = "com.opdar.seed.sql.username";
    public static String JDBC_PASSWORD = "com.opdar.seed.sql.password";
    public static String JDBC_DRIVER = "com.opdar.seed.sql.driver";

    void onCreate();
    void onDestory();
    String get(String key);
}
