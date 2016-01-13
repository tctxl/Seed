package com.opdar.seed.io.base;

import com.opdar.framework.web.SeedWeb;
import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.cluster.ClusterHandler;
import com.opdar.seed.io.handler.Handler;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Initializer extends ChannelInitializer<SocketChannel> {
    protected static final Decoder DECODER = new Decoder();
    protected static final Encoder ENCODER = new Encoder();
    protected static final Handler HANDLER = new Handler();
    protected static final ClusterHandler CLUSTER_HANDLER = new ClusterHandler();
    private boolean isClient = false;
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private com.opdar.seed.io.IOPlugin ioPlugin;

    public Initializer(SeedWeb web) {
        this(web,false);
    }

    public Initializer(SeedWeb web,boolean isClient) {
        HANDLER.setSeedWeb(web);
        this.isClient = isClient;
    }

    public boolean isClient() {
        return isClient;
    }

    public Initializer setIsClient(boolean isClient) {
        this.isClient = isClient;
        return this;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", DECODER);
        pipeline.addLast("encoder", ENCODER);
        pipeline.addLast("handler", HANDLER.setIOPlugin(ioPlugin));
        if (TokenUtil.contains(new byte[]{'c'}) || isClient) {
            pipeline.addLast("clusters", CLUSTER_HANDLER.setIOPlugin(ioPlugin));
        }
    }

    public Initializer setIOPlugin(IOPlugin ioPlugin) {
        this.ioPlugin = ioPlugin;
        return this;
    }

    public IOPlugin getIOPlugin() {
        return ioPlugin;
    }
}
