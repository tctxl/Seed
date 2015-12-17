package com.opdar.seed.io.hole;

import com.opdar.seed.io.base.Callback;
import com.opdar.seed.io.p2p.P2pServer;
import com.opdar.seed.io.utils.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class HoleMaker implements Runnable, Callback<DatagramPacket, Object> {

    public static final String SUCCESS = "SUCCESS";
    private String hostName;
    private InetSocketAddress server;
    private Callback<DatagramPacket, Object> callback;
    ZooKeeper zk = null;
    private static final String HOST = "192.168.1.242";

    public HoleMaker(String hostName, InetSocketAddress server) {
        this.hostName = hostName;
        this.server = server;
        try {
            zk = new ZooKeeper(HOST, 2000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("changed!");
                    System.out.println(watchedEvent.getPath());
                    System.out.println(watchedEvent.getState());
                }
            });
            ZookeeperUtils.createPath(zk, "/seed", "");
            ZookeeperUtils.createPath(zk, "/seed/io", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/server", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/server/" + hostName, "", CreateMode.EPHEMERAL);
            setMessageCallback(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setMessageCallback(Callback<DatagramPacket, Object> callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] buf = new byte[P2pServer.UDP_BUFFER_SIZE];
            DatagramPacket inPacket = new DatagramPacket(buf, 0, buf.length);
            byte[] data = hostName.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, server);
            socket.send(packet);
            socket.receive(inPacket);
            String result = new String(inPacket.getData(),"utf-8").trim();
            if (result.equals(SUCCESS)) {
                while (true) {
                    socket.receive(inPacket);
                    if (callback != null) {
                        Object backResult = callback.call(inPacket);
//                        socket.send(backResult);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Override
    public Object call(DatagramPacket object) {
        System.out.println(object.getLength());
        return null;
    }

    public static void main(String[] args) {
        HoleMaker maker = new HoleMaker("HOST-1", new InetSocketAddress("180.175.15.232", 1011));
        new Thread(maker).start();
    }
}
