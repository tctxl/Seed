package com.opdar.seed.io.p2p;

import java.util.HashMap;

/**
 * Created by 俊帆 on 2015/9/16.
 */
public class P2PClusterPool {

    private static HashMap<String, P2pClient> cluster = new HashMap<String, P2pClient>();

    public static P2pClient get(String serverName) {
        return cluster.get(serverName);
    }

    public static void join(final String ip, final Integer port, final String serverName) {
        cluster.put(serverName, new P2pClient(ip, port));
    }
}
