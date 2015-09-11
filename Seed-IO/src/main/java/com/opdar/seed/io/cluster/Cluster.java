package com.opdar.seed.io.cluster;

import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.ClusterProtocol;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 集群节点，管理集群在线状态，消息通信
 * Created by 俊帆 on 2015/8/31.
 */
public class Cluster {
    private static final Logger logger = LoggerFactory.getLogger(Cluster.class);
    public static int CONNECTED = 0;
    public static int AUTO_RECONNECT = 1;
    private String ip;
    private int port;
    private ChannelHandlerContext ctx;
    private String socketId;
    private boolean isOverTime = false;
    private long heartTime = 0;

    private static final int OVERTIME = 60;

    public Cluster(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        if (address != null) {
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
        ctx.close();
    }

    public void write(byte[] bytes) {
        ctx.writeAndFlush(bytes).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    clearHeartbeat();
                }
            }
        });
    }

    public void reacher() {
        ClusterProtoc.Message message = ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.REACHER).build();
        write(ClusterProtocol.create(message));
    }

    public void heartbeat() {
        if(heartTime == 0){
            logger.debug("Start Heartbeat!");
            ctx.executor().schedule(heartbeat, OVERTIME, TimeUnit.SECONDS);
        }else{
            clearHeartbeat();
        }
    }

    private Runnable heartbeat = new Runnable() {
        @Override
        public void run() {
            if (!ctx.channel().isOpen()) {
                return;
            }
            long sec = (System.currentTimeMillis() - heartTime)/1000;
            if(sec < 0)sec = OVERTIME;
            if(sec < OVERTIME){
                sec = OVERTIME - sec;
            }else{
                sec = OVERTIME;
                if(isOverTime){
                    downline();
                }else{
                    write(ClusterProtocol.create(ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.HEARTBEAT).build()));
                    isOverTime = true;
                }
            }
            ctx.executor().schedule(this, sec, TimeUnit.SECONDS);
            logger.debug("heartbeat::next sec {} , isOverTime [{}]",sec,isOverTime);
        }

    };

    public void clearHeartbeat() {
        heartTime = System.currentTimeMillis();
        isOverTime = false;
    }
}
