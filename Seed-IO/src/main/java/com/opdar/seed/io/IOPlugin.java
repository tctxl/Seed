package com.opdar.seed.io;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.DefaultSupport;
import com.opdar.framework.utils.Plugin;
import com.opdar.framework.web.SeedWeb;
import com.opdar.seed.io.base.Initializer;
import com.opdar.seed.io.token.ActionToken;
import com.opdar.seed.io.token.MethodToken;
import com.opdar.seed.io.token.Token;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultPromise;

/**
 * Created by 俊帆 on 2015/8/27.
 */
public class IOPlugin extends DefaultSupport implements Plugin {
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
    private ChannelFuture channelFuture;
    private SeedWeb web = new SeedWeb();
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private int port;

    public IOPlugin(int port) {
        this.port = port;
    }

    public void loadConfig(IConfig config){
        if(config != null){
            web.setClassLoader(classLoader);
            super.loadConfig(config,web);
        }
    }

    @Override
    public boolean install() throws Exception {
        loadToken(MethodToken.class);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new Initializer(web));
        channelFuture = b.bind(port).sync().channel().closeFuture().sync();
        return true;
    }

    public IOPlugin loadToken(Class<? extends Token> tokenClz) {
        try {
            TokenUtil.add(tokenClz.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public boolean uninstall() {
        if(channelFuture instanceof DefaultPromise){
            ((DefaultPromise) channelFuture).setUncancellable();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        return true;
    }

    public static void main(String[] args) {
        Plugin plugin = new IOPlugin(1080).loadToken(ActionToken.class);
        try {
            boolean ret = plugin.install();
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.uninstall();
        }
    }

    @Override
    public ISupport start() {
        try {
            install();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
