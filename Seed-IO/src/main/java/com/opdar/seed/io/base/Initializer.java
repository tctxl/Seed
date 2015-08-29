package com.opdar.seed.io.base;

import com.opdar.framework.web.SeedWeb;
import com.opdar.seed.io.handler.Handler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


public class Initializer extends ChannelInitializer<SocketChannel> {
    protected static final Decoder DECODER = new Decoder();
    protected static final Encoder ENCODER = new Encoder();
    protected static final Handler HANDLER = new Handler();

    public Initializer(SeedWeb web) {
        HANDLER.setSeedWeb(web);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("heartbeat",new IhygeiaHeartbeat(Constants.Params.READ_IDLE_TIME,Constants.Params.WRITE_IDLE_TIME,Constants.Params.ALL_IDLE_TIME));
        //add framer
//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
//                163840, new ByteBuf[] {
//                        Unpooled.wrappedBuffer(new byte[] { '4', '3' ,'6',':',':'}),
//                        Unpooled.wrappedBuffer(new byte[] { '\n' })
//                }));
        pipeline.addLast("decoder", DECODER);
        pipeline.addLast("encoder", ENCODER);
        pipeline.addLast("handler", HANDLER);
    }
}
