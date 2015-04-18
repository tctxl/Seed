package com.opdar.framework.server.supports.netty;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.DefaultSupport;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class NettySupport extends DefaultSupport {

    private int port = 8080;
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() *2);
    private ChannelFuture channelFuture;

    public NettySupport(int port){
        this.port = port;
    }

    @Override
    public ISupport start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new SeedNettyInitializer());
                try {
                    String p = NettySupport.config.get(IConfig.PORT);
                    if(p != null)
                        port = Integer.valueOf(p);
                    channelFuture = b.bind(port);
                    channelFuture = channelFuture.sync();
                    channelFuture = channelFuture.channel().closeFuture();
                    channelFuture = channelFuture.sync();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
//                    stop();
                }
            }
        }).start();
        return this;
    }

}
