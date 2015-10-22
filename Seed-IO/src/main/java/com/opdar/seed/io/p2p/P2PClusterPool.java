package com.opdar.seed.io.p2p;

import com.opdar.seed.io.messagepool.MessagePool;

import java.util.HashMap;

/**
 * Created by 俊帆 on 2015/9/16.
 */
public class P2PClusterPool {

    private static HashMap<String, P2pClient> cluster = new HashMap<String, P2pClient>();

    private static MessagePool<P2pClient> clusterPool = new MessagePool<P2pClient>() {
        @Override
        public boolean set(P2pClient object) {
            cluster.put(object.getName(),object);
            return true;
        }

        @Override
        public P2pClient get(String name) {
            return cluster.get(name);
        }

        @Override
        public boolean del(String name) {
            cluster.remove(name);
            return true;
        }
    };

    public static void setClusterPool(MessagePool<P2pClient> clusterPool) {
        P2PClusterPool.clusterPool = clusterPool;
    }

    public static P2pClient get(String serverName) {
        return clusterPool.get(serverName);
    }

    public static void join(final String ip, final Integer port, final String serverName) {
        clusterPool.set(new P2pClient(ip, port).setName(serverName));
    }
}
