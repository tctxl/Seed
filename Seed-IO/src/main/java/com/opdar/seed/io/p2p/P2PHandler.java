package com.opdar.seed.io.p2p;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.base.IoSession;
import com.opdar.seed.io.cluster.Cluster;
import com.opdar.seed.io.cluster.ClusterPool;
import com.opdar.seed.io.protocol.ActionProtocol;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.protocol.OnlineProtoc;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by 俊帆 on 2015/9/16.
 */
@ChannelHandler.Sharable
public class P2PHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(P2PHandler.class);
    IOPlugin ioPlugin;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
            logger.error(cause.getMessage());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        IoSession session = new IoSession(ctx);
        if (object instanceof MessageProtoc.Action) {
            //终端
            if (ioPlugin != null && ioPlugin.getMessageCallback() != null)
                ioPlugin.getMessageCallback().callback(((MessageProtoc.Action) object).getType(), ((MessageProtoc.Action) object).getMessageId(), session);
        } else {
            //终端
            ioPlugin.getMessageCallback().otherMessage(object, session);
        }
    }

    public IOPlugin getIoPlugin() {
        return ioPlugin;
    }

    public P2PHandler setIoPlugin(IOPlugin ioPlugin) {
        this.ioPlugin = ioPlugin;
        return this;
    }
}
