package com.opdar.seed.io.utils;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class ZookeeperUtils {

    public static void createPath(ZooKeeper zk, String path, String value) throws KeeperException, InterruptedException {
        ZookeeperUtils.createPath(zk, path, value, null);
    }

    public static void createPath(ZooKeeper zk, String path, String value, CreateMode mode) throws KeeperException, InterruptedException {
        Stat ret = zk.exists(path, false);
        if (ret == null) {
            if (mode == null) mode = CreateMode.PERSISTENT;
            zk.create(path, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        }
    }
}
