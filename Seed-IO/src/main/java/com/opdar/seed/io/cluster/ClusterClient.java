package com.opdar.seed.io.cluster;

import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.ClusterProtocol;
import com.opdar.seed.io.token.ClusterToken;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2015/9/15.
 */
public class ClusterClient {

    private int AUTO_RECONNECT = 1;
    private Channel ch = null;
    private final ExecutorService es = Executors.newSingleThreadExecutor();
    private final Logger logger = LoggerFactory.getLogger(ClusterClient.class);
    private boolean isStop = false;
    private boolean isConnected = false;
    public ClusterClient(String host, Integer port) {
        connect(host, port);
    }

    public ClusterClient(String host, int port) {
        connect(host, port);
    }

    public void stop() {
        isStop = true;
        es.shutdown();
        if(ch != null){
            ch.close();
            ((NioSocketChannel) ch).shutdownOutput();
        }
    }

    public void send(final byte[] data){
        ch.writeAndFlush(data).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    logger.debug("发送失败，正在重试！");
                    send(data);
                }
            }
        });
    }

    public void connect(final String host, final int port) {
        if(isStop)return;
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClusterInitializer());
            logger.debug("正在连接服务...");
            ch = b.connect(host, port).sync().channel();
            isConnected = true;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(ch!=null)
                        ch.closeFuture().sync();
                    isConnected = false;
                    logger.debug("链路已断开!");
                } catch (Exception e) {
                    logger.error(e.toString());
                } finally {
                    group.shutdownGracefully();
                    if (AUTO_RECONNECT == 1) {
                        logger.debug("正在进行重连...");
                        connect(host, port);
                    }
                }
            }
        });
        TokenUtil.add(new ClusterToken());
        group.schedule(new Runnable() {
            @Override
            public void run() {
                logger.debug("检查连接状态...[{}]", !isConnected? "失败" : "成功");
                if (AUTO_RECONNECT == 0) {
                    ch.close();
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }
}
