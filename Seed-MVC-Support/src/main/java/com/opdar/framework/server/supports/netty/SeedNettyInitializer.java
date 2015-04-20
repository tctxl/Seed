package com.opdar.framework.server.supports.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


public class SeedNettyInitializer extends ChannelInitializer<SocketChannel> {
    private static final SeedHttpHandler HANDLER = new SeedHttpHandler();
    public SeedNettyInitializer() {
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpObjectAggregator(1048576));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpResponseEncoder());
//        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast(HANDLER);
    }
}
