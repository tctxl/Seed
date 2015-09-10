package com.opdar.seed.io.cluster;

import com.opdar.seed.io.base.Decoder;
import com.opdar.seed.io.base.Encoder;
import com.opdar.seed.io.handler.Handler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by 俊帆 on 2015/9/7.
 */
public class ClusterInitializer extends ChannelInitializer<SocketChannel> {
    protected static final Decoder DECODER = new Decoder();
    protected static final Encoder ENCODER = new Encoder();
    protected static final Handler HANDLER = new Handler();
    protected static final ClusterHandler CLUSTER_HANDLER = new ClusterHandler();

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", DECODER);
        pipeline.addLast("encoder", ENCODER);
        pipeline.addLast("handler", HANDLER);
        pipeline.addLast("clusters", CLUSTER_HANDLER);
    }
}
