package com.opdar.seed.io.utils;

import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class CacheUtils {
    public SSDB ssdb = null;

    public static String HOST = "localhost";
    public static Integer PORT = 8888;
    public static Integer TIMEOUT = 2000;

    private static CacheUtils cacheUtils = null;

    public static CacheUtils getInstance(){
        if(cacheUtils == null){
            cacheUtils = new CacheUtils(HOST,PORT,TIMEOUT);
        }
        return cacheUtils;
    }

    public CacheUtils(String host,int port,int timeout) {
        ssdb = SSDBs.pool(host, port, timeout, null);
        if (!ssdb.ping().ok()) {
            System.out.println("SSDB NOT OK");
        }
    }

    public void cache(String key, Object value, int timeout) {
        if (timeout == -1) {
            ssdb.set(key, value);
        } else {
            ssdb.setx(key, value, timeout);
        }
    }

    public boolean hashCache(String key1, String key2, Object value) {
        return ssdb.hset(key1, key2, value).ok();
    }

    public Response hashGet(String key1, String key2) {
        return ssdb.hget(key1, key2);
    }

    public boolean hashDel(String key1, String key2) {
        return ssdb.hdel(key1, key2).ok();
    }

    public Response getCache(String key) {
        return ssdb.get(key);
    }

    public Response del(String... key) {
        return ssdb.multi_del(key);
    }

    public boolean exist(String key) {
        return ssdb.exists(key).asInt() > 0;
    }

    public Response expire(String key, int timeout) {
        return ssdb.expire(key, timeout);
    }

    public static void main(String[] args) {
        CacheUtils.PORT = 18888;
        CacheUtils.getInstance().ssdb.flushdb("");
    }

}
