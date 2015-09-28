package com.opdar.seed.io.p2p;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.token.ActionToken;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class P2pServer implements Runnable{
    int port;
    IOPlugin ioPlugin;
    public P2pServer(int port,IOPlugin ioPlugin){
        this.port = port;
        this.ioPlugin = ioPlugin;
    }

    public void start(){
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
            b.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
    }
}
