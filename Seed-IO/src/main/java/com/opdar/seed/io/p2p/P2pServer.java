package com.opdar.seed.io.p2p;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.token.ActionToken;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class P2pServer implements Runnable {
    int port;
    IOPlugin ioPlugin;
    public static final int UDP_BUFFER_SIZE = 2048;
    String host = "121.199.40.54";

    public P2pServer(int port, IOPlugin ioPlugin) {
        this.port = port;
        this.ioPlugin = ioPlugin;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        TokenUtil.add(new ActionToken());
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new P2pInitializer().setIOPlugin(ioPlugin));
            Channel channel = b.bind(port).sync().channel();
            if (channel instanceof NioDatagramChannel) {
                ChannelConfig config = channel.config();
                if (config instanceof DefaultDatagramChannelConfig) {
                    ((DefaultDatagramChannelConfig) config).setRecvByteBufAllocator(new FixedRecvByteBufAllocator(UDP_BUFFER_SIZE));
                }
            }
            if(TokenUtil.contains(new byte[]{'c'})){
                channel.eventLoop().schedule(new FailureMessageScanRunnable(ioPlugin,channel),FailureMessageScanRunnable.OUTTIME, FailureMessageScanRunnable.TIMEUNIT);
            }
//            ZookeeperUtils.createPath(zk, "/seed", "");
//            ZookeeperUtils.createPath(zk, "/seed/io", "");
//            ZookeeperUtils.createPath(zk, "/seed/io/udp", "");
//            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole", "");
//            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/server", "");
//            ZookeeperUtils.createPath(zk, "/seed/io/udp/hole/server/" + ioPlugin.getServerName(), "", CreateMode.EPHEMERAL);
//            channel.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(String.format("HOST:%s", ioPlugin.getServerName()).getBytes()), new InetSocketAddress(host, 1011)));
            channel.closeFuture().await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
    }
}
