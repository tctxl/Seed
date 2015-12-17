package com.opdar.seed.io.hole;

import com.opdar.seed.io.p2p.P2pServer;
import com.opdar.seed.io.utils.ZookeeperUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class HoleServer implements Runnable {
    int port;
    String host;
    ZooKeeper zk = null;
    private static final Logger logger = LoggerFactory.getLogger(HoleServer.class);

    public HoleServer(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() {
        try {
            zk = new ZooKeeper(host, 5000, null);

            ZookeeperUtils.createPath(zk, "/seed", "");
            ZookeeperUtils.createPath(zk, "/seed/io", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole", "");
            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/server", "");
            zk.getChildren("/seed/io/udp/hole/server", new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println(watchedEvent.getPath());
                    System.out.println(watchedEvent.getType());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket(1780);
                    while (true){
                        byte[] buf = new byte[2048];
                        DatagramPacket p = new DatagramPacket(buf,2048);
                        socket.receive(p);

                        logger.info("remote address : ",socket.getRemoteSocketAddress());
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new HoleHandler(zk));
            Channel channel = b.bind(port).sync().channel();
            if (channel instanceof NioDatagramChannel) {
                ChannelConfig config = channel.config();
                if (config instanceof DefaultDatagramChannelConfig) {
                    ((DefaultDatagramChannelConfig) config).setRecvByteBufAllocator(new FixedRecvByteBufAllocator(P2pServer.UDP_BUFFER_SIZE));
                }
            }
            channel.closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        String server = null;
        if(args.length > 0){
            server = args[0];
        }else{
            server = "121.199.40.54";
        }
        new HoleServer(1011,server).start();
    }
}
