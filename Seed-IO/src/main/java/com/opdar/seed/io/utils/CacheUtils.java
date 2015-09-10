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
    static SSDB ssdb = null;

    static {
        ssdb = SSDBs.pool("localhost", 8888, 2000, null);
        if (!ssdb.ping().ok()) {
            System.out.println("SSDB NOT OK");
        }
    }

    public static void cache(String key, Object value, int timeout) {
        if (timeout == -1) {
            ssdb.set(key, value);
        } else {
            ssdb.setx(key, value, timeout);
        }
    }

    public static boolean hashCache(String key1, String key2, Object value) {
        return ssdb.hset(key1, key2, value).ok();
    }

    public static Response hashGet(String key1, String key2) {
        return ssdb.hget(key1, key2);
    }

    public static boolean hashDel(String key1, String key2) {
        return ssdb.hdel(key1, key2).ok();
    }

    public static Response getCache(String key) {
        return ssdb.get(key);
    }

    public static Response del(String... key) {
        return ssdb.multi_del(key);
    }

    public static boolean exist(String key) {
        return ssdb.exists(key).asInt() > 0;
    }

    public static Response expire(String key, int timeout) {
        return ssdb.expire(key, timeout);
    }

    public static void main(String[] args) {
        ssdb.flushdb("");
    }

}
