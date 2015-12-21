package com.opdar.seed.io.messagepool;

import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.utils.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by 俊帆 on 2015/12/17.
 */
public class ZookeeperPool implements MessagePool<ZookeeperPool.ZooResult>, Watcher {

    private String host;
    private int port = 2181;
    private ZooKeeper zooKeeper;
    private String path;

    public static class ZooResult {
        String path;
        List<String> children;
        byte[] data;
        CreateMode createMode = CreateMode.PERSISTENT;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<String> getChildren() {
            return children;
        }

        public void setChildren(List<String> children) {
            this.children = children;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public CreateMode getCreateMode() {
            return createMode;
        }

        public void setCreateMode(CreateMode createMode) {
            this.createMode = createMode;
        }
    }

    public ZookeeperPool(String host) {
        this.host = host;
        init();
    }

    public ZookeeperPool(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setPath(String path) {
        this.path = Utils.testRouter(path);
    }

    public void createPath() {
        createPath(CreateMode.PERSISTENT);
    }

    public void createPath(CreateMode createMode) {
        try {
            String _path = path;
            if (_path.length() > 1) {
                _path = _path.substring(1);
            }
            String[] paths = _path.split("/");
            _path = "";
            for (int i = 0; i < paths.length; i++) {
                String p = paths[i];
                _path += "/" + p;
                ZookeeperUtils.createPath(zooKeeper, _path, "", createMode);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String path = "/seed/io/udp/server/";
        System.out.println(new ZookeeperPool("192.168.1.158",2181).get("/"));
    }

    public void init() {
        try {
            zooKeeper = new ZooKeeper(host, 1000, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean set(ZooResult object) {
        try {
            Stat ret = zooKeeper.exists(object.path, false);
            if (ret == null) {
                ZookeeperUtils.createPath(zooKeeper, object.path, object.data, object.createMode);
            } else {
                zooKeeper.setData(object.path, object.data, -1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public ZooResult get(String path) {
        try {
            ZooResult result = new ZooResult();
            result.path = path;
            result.data = zooKeeper.getData(path, null, null);
            result.children = zooKeeper.getChildren(path, null);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean del(String path) {
        try {
            zooKeeper.delete(path, -1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Pool : "+watchedEvent);
    }
}
