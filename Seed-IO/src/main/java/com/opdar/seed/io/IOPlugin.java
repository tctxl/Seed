package com.opdar.seed.io;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.DefaultSupport;
import com.opdar.framework.utils.Plugin;
import com.opdar.framework.web.SeedWeb;
import com.opdar.seed.io.base.Initializer;
import com.opdar.seed.io.base.IoSession;
import com.opdar.seed.io.base.SessionStateCallback;
import com.opdar.seed.io.cluster.ClusterPool;
import com.opdar.seed.io.flash.FlashPolicyServer;
import com.opdar.seed.io.messagepool.MessagePool;
import com.opdar.seed.io.messagepool.SSDBMessagePool;
import com.opdar.seed.io.messagepool.SSDBOnlinePool;
import com.opdar.seed.io.p2p.P2pServer;
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

import java.util.UUID;

/**
 * Created by 俊帆 on 2015/8/27.
 */
public class IOPlugin extends DefaultSupport implements Plugin {
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
    private ChannelFuture channelFuture;
    private SeedWeb web = new SeedWeb();
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private String serverName;
    private SessionStateCallback sessionStateCallback;
    private FlashPolicyServer flashPolicyServer = new FlashPolicyServer();
    private boolean isOpenFlashPolicy = false;

    public interface MessageCallback {
        void callback(MessageProtoc.Action.Type type, String messageId, IoSession session);

        void otherMessage(Object o, IoSession session);
    }

    public void setFlashPolicy(boolean isOpenFlashPolicy) {
        this.isOpenFlashPolicy = isOpenFlashPolicy;
    }

    private MessageCallback callback;
    public static String CLUSTER_HOST = "";
    public static int CLUSTER_PORT = 0;
    private MessagePool<ClusterProtoc.Message> msgPool = SSDBMessagePool.getInstance();
    private MessagePool<ClusterProtoc.Message> msgFailurePool = SSDBMessagePool.getInstance();
    private MessagePool<OnlineProtoc.Online> onlinePool = SSDBOnlinePool.getInstance();
    private int port;
    private boolean isP2P = false;

    public String getServerName() {
        if (serverName == null) serverName = UUID.randomUUID().toString();
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public boolean isP2P() {
        return isP2P;
    }

    public void setIsP2P(boolean isP2P) {
        this.isP2P = isP2P;
    }

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

    public IOPlugin setSessionStateCallback(SessionStateCallback sessionStateCallback) {
        this.sessionStateCallback = sessionStateCallback;
        return this;
    }

    public MessagePool<ClusterProtoc.Message> getMsgFailurePool() {
        return msgFailurePool;
    }

    public void setMsgFailurePool(MessagePool<ClusterProtoc.Message> msgFailurePool) {
        this.msgFailurePool = msgFailurePool;
    }

    public SessionStateCallback getSessionStateCallback() {
        return sessionStateCallback;
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
        Initializer initializer = new Initializer(web).setIOPlugin(this);
        if (isP2P) {
            new P2pServer(port, this).start();
        } else if (!TokenUtil.contains(new byte[]{'c'})) {
            ClusterPool.join(IOPlugin.CLUSTER_HOST, IOPlugin.CLUSTER_PORT, getServerName(), initializer.setIsClient(true));
        }
        if(isOpenFlashPolicy){
            flashPolicyServer.start();
        }
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(initializer);
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
        if(isOpenFlashPolicy){
            flashPolicyServer.shutdown();
        }
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
