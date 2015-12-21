package com.opdar.seed.io.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class P2pClient {
    private String host;
    private Integer port;
    private String name;
    private Callback callback;
    private Integer timeOut = 0;

    public String getHost() {
        return host;
    }

    public P2pClient setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public P2pClient setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getName() {
        return name;
    }

    public P2pClient setName(String name) {
        this.name = name;
        return this;
    }

    public interface Callback {
        void back();
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public P2pClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public P2pClient() {
    }

    DatagramSocket socket;

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public void send(byte[] data) {
        try {
            socket = new DatagramSocket();
            if (timeOut > 0) socket.setSoTimeout(timeOut);
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data, String host, Integer port) {
        send(data, host, port, 0);
    }

    public void send(byte[] data, String host, Integer port, Integer port2) {
        try {
            if (port2 == null) port2 = 0;
            socket = new DatagramSocket(port2);
            System.out.println("LocalPort : " + socket.getLocalPort());
            byte[] buf = new byte[2048];
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String receive() throws IOException {
        byte[] buf = new byte[2048];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        socket.receive(p);
        return new String(buf).trim();
    }

    public void close() {
        if (socket != null)
            socket.close();
    }

    protected static final Logger logger = LoggerFactory.getLogger(P2pClient.class);

//    public static void main(String[] args) throws Exception {
//        String ClientName = "CLIENT-1";
//        String ServerName = null;
//        ZooKeeper zk = new ZooKeeper(ZOOKEEPER, 5000, new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//
//            }
//        });
//        ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/client", "");
//        ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/client/" + ClientName, "", CreateMode.EPHEMERAL);
//
//        //请求打洞服务：获取NAT端口
//        P2pClient client = new P2pClient(HOST, 1011);
//        client.send(("CLIENT_HOST:" + ClientName).getBytes());
//        ServerName = client.receive();
//        byte[] data = zk.getData("/seed/io/udp/hole/server/" + ServerName, null, null);
//        String[] serverDesc = new String(data).split("\\|");
//        byte[] clientData = zk.getData("/seed/io/udp/hole/client/" + ClientName, null, null);
//        String[] clientDesc = new String(clientData).split("\\|");
////        //访问1080：另一端NAT端口，该包将被抛弃
//        logger.info("SERVER[{}:{}]", serverDesc[0], Integer.valueOf(serverDesc[1]));
//        System.out.println("Client Port : " + Integer.valueOf(clientDesc[1]));
//        client.close();
//        client.send(P2pProtocol.create("DIGPORT", ClientName), serverDesc[0], Integer.valueOf(serverDesc[1]), Integer.valueOf(clientDesc[1]));
////        Thread.sleep(5000);
//        try {
////            String result = client.receive();
////            System.out.println("RESULT : "+result);
//        } catch (Exception e) {
//        }
////        //通知打包服务器：要求1080端主动访问HOST
////        client.send(P2pProtocol.create("DIGPORT",ClientName),serverDesc[0], Integer.valueOf(serverDesc[1]),Integer.valueOf(clientDesc[1]));
//
//        P2pClient client3 = new P2pClient();
//        client3.send(("DIGPORT:" + ServerName + "," + ClientName).getBytes(), HOST, 1011);
//        String result = client.receive();
//        System.out.println("RESULT2 : " + result);
//    }
}
