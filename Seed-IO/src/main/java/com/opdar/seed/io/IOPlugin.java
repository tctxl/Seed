package com.opdar.seed.io;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.DefaultSupport;
import com.opdar.framework.utils.Plugin;
import com.opdar.framework.web.SeedWeb;
import com.opdar.seed.io.act.Act;
import com.opdar.seed.io.base.Initializer;
import com.opdar.seed.io.base.IoSession;
import com.opdar.seed.io.cluster.ClusterPool;
import com.opdar.seed.io.messagepool.MessagePool;
import com.opdar.seed.io.messagepool.SSDBMessagePool;
import com.opdar.seed.io.messagepool.SSDBOnlinePool;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.protocol.OnlineProtoc;
import com.opdar.seed.io.token.*;
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

    public interface MessageCallback {
        void callback(MessageProtoc.Action.Type type, String messageId, IoSession session);
    }

    private MessageCallback callback;
    public static String CLUSTER_HOST = "";
    public static int CLUSTER_PORT = 0;
    private MessagePool<ClusterProtoc.Message> msgPool = SSDBMessagePool.getInstance();
    private MessagePool<OnlineProtoc.Online> onlinePool = SSDBOnlinePool.getInstance();
    private int port;

    public MessagePool<ClusterProtoc.Message> getMsgPool() {
        return msgPool;
    }

    public void setMsgPool(MessagePool<ClusterProtoc.Message> msgPool) {
        this.msgPool = msgPool;
    }

    public MessagePool<OnlineProtoc.Online> getOnlinePool() {
        return onlinePool;
    }

    public void setOnlinePool(MessagePool<OnlineProtoc.Online> onlinePool) {
        this.onlinePool = onlinePool;
    }

    public MessageCallback getMessageCallback() {
        return this.callback;
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        this.callback = messageCallback;
    }

    public IOPlugin(int port) {
        this.port = port;
    }

    public void loadConfig(IConfig config) {
        if (config != null) {
            web.setClassLoader(classLoader);
            super.loadConfig(config, web);
        }
    }

    @Override
    public boolean install() throws Exception {
        loadToken(MethodToken.class);
        if (!TokenUtil.contains('c')) {
            ClusterPool.join(IOPlugin.CLUSTER_HOST, IOPlugin.CLUSTER_PORT);
        }
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new Initializer(web).setIOPlugin(this));
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
        if (channelFuture instanceof DefaultPromise) {
            ((DefaultPromise) channelFuture).setUncancellable();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        return true;
    }

    public IOPlugin setClusterPool(String host, int port) {
        CLUSTER_HOST = host;
        CLUSTER_PORT = port;
        return this;
    }

    public static void main(String[] args) {
        Plugin plugin = new IOPlugin(1080)
                .loadToken(ActionToken.class)
                .loadToken(ClusterToken.class);
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
