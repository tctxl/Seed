package com.opdar.seed.io.p2p;

import com.opdar.seed.io.base.Decoder;
import com.opdar.seed.io.base.Encoder;
import com.opdar.seed.io.cluster.ClusterHandler;
import com.opdar.seed.io.handler.Handler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by 俊帆 on 2015/9/7.
 */
public class P2pInitializer extends ChannelInitializer<NioDatagramChannel> {
    protected static final DatagramDecoder DECODER1 = new DatagramDecoder();
    protected static final Decoder DECODER2 = new Decoder();
    protected static final Encoder ENCODER = new Encoder();
    protected static final Handler HANDLER = new Handler();
    protected static final ClusterHandler CLUSTER_HANDLER = new ClusterHandler();

    @Override
    public void initChannel(NioDatagramChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder1",DECODER1);
        pipeline.addLast("decoder2", DECODER2);
        pipeline.addLast("encoder", ENCODER);
        pipeline.addLast("handler", HANDLER);
        pipeline.addLast("clusters", CLUSTER_HANDLER);
    }
}
