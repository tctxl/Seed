package com.opdar.seed.io.flash;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultPromise;

/**
 * Created by 俊帆 on 2015/12/30.
 */
public class FlashPolicyServer implements Runnable {
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
    private ChannelFuture channelFuture;
    public void start(){
        new Thread(this).start();
    }

    public void shutdown(){
        if (channelFuture instanceof DefaultPromise) {
            ((DefaultPromise) channelFuture).setUncancellable();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void run() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new FlashInitializer());
            channelFuture = b.bind(843).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
