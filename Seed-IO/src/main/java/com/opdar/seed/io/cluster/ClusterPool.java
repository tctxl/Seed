package com.opdar.seed.io.cluster;

import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.ClusterProtocol;
import com.opdar.seed.io.token.ClusterToken;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2015/8/31.
 */
public class ClusterPool {
    private static HashMap<String, Cluster> cluster = new HashMap<String, Cluster>();
    private static final Logger logger = LoggerFactory.getLogger(ClusterPool.class);
    private static final ExecutorService es = Executors.newSingleThreadExecutor();
    public static void add(Cluster address) {
        cluster.put(address.getIp() + ":" + address.getPort(), address);
    }

    public static Cluster get(String address, String port) {
        return get(address + ":" + port);
    }

    public static Cluster get(String address) {
        return cluster.get(address);
    }
    /**
     * 通知主机
     *
     * @param address
     */
    public static void request(Cluster address) {

    }

    private static Channel ch = null;

    /**
     * 设置master的ip与地址
     *
     * @param ip
     * @param port
     */
    public static void join(final String ip, final Integer port) {
        //创建socket并连接至Cluster Group
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClusterInitializer());

            ch = b.connect(ip, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ch.closeFuture().sync();
                    System.out.println("链路已断开!");
                    if(Cluster.AUTO_RECONNECT == 1){
                        logger.debug("正在进行重连...");
                        join(ip, port);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }
            }
        });
        TokenUtil.add(new ClusterToken());
        ClusterProtoc.Message systemDo = ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.JOIN).build();
        ch.writeAndFlush(ClusterProtocol.create(systemDo));
        group.schedule(new Runnable() {
            @Override
            public void run() {
                logger.debug("检查连接状态...[{}]", Cluster.CONNECTED == 0 ? "失败" : "成功");
                if(Cluster.AUTO_RECONNECT == 0){
                    ch.close();
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        ClusterPool.join("localhost", 1080);
    }

    public static void remove(Cluster cluster) {
        ClusterPool.cluster.remove(cluster.getIp() + ":" + cluster.getPort());
    }
}
