package com.opdar.seed.io.cluster;

import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.ClusterProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * 集群节点，管理集群在线状态，消息通信
 * Created by 俊帆 on 2015/8/31.
 */
public class Cluster {
    public static int CONNECTED = 0;
    public static int AUTO_RECONNECT = 1;
    private String ip;
    private int port;
    private ChannelHandlerContext ctx;
    private String socketId;

    public Cluster(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        if(address != null){
            ip = address.getHostName();
            port = address.getPort();
        }
        this.ctx = ctx;
        ClusterPool.add(this);
        socketId = UUID.randomUUID().toString();
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void request() {

    }

    public void downline() {
        ClusterPool.remove(this);
    }

    public void write(byte[] bytes) {
        ctx.writeAndFlush(bytes);
    }

    public void reacher() {
        ClusterProtoc.Message message = ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.REACHER).build();
        write(ClusterProtocol.create(message));
    }
}
