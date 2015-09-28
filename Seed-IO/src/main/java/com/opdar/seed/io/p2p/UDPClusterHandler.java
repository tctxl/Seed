package com.opdar.seed.io.p2p;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.cluster.Cluster;
import com.opdar.seed.io.cluster.ClusterPool;
import com.opdar.seed.io.messagepool.SSDBMessagePool;
import com.opdar.seed.io.protocol.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by 俊帆 on 2015/9/14.
 */
public class UDPClusterHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(UDPClusterHandler.class);
    private com.opdar.seed.io.IOPlugin IOPlugin;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
            logger.error(cause.getMessage());
            ctx.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {
        if (object instanceof ClusterProtoc.Message) {
            switch (((ClusterProtoc.Message) object).getAct()) {
                case MESSAGE: {
                    //get a message id
                    String messageId = ((ClusterProtoc.Message) object).getMessageId();
                    //use id to get a message from message pool
                    ClusterProtoc.Message message = IOPlugin.getMsgPool().get(messageId);
                    String to = message.getTo();
                    //
                    OnlineProtoc.Online online = IOPlugin.getOnlinePool().get(to);

//                    P2pClient client = new P2pClient(online.getHost(),online.getPort());
//                    byte[] buffer = ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.MSG).setMessageId(messageId).build());
//                    client.send(buffer);
//                    ctx.writeAndFlush(ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.INQUEUE).setMessageId(messageId).build()));
                }
                break;
            }
        }
    }

    public UDPClusterHandler setIOPlugin(com.opdar.seed.io.IOPlugin IOPlugin) {
        this.IOPlugin = IOPlugin;
        return this;
    }

    public IOPlugin getIOPlugin() {
        return IOPlugin;
    }
}
