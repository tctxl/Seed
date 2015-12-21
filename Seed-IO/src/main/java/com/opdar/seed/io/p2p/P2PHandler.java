package com.opdar.seed.io.p2p;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.base.Result;
import com.opdar.seed.io.base.UdpIoSession;
import com.opdar.seed.io.hole.HoleMaker;
import com.opdar.seed.io.protocol.Command;
import com.opdar.seed.io.protocol.MessageProtoc;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by 俊帆 on 2015/9/16.
 */
@ChannelHandler.Sharable
public class P2PHandler extends SimpleChannelInboundHandler<Result> {

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
    public void channelRead0(ChannelHandlerContext ctx, Result result) throws Exception {
        UdpIoSession session = new UdpIoSession(ctx, result.getAddress());
        Object object = result.get();
//        logger.info("SENDER : [{}]", result.getAddress());
//        if (object instanceof Command) {
//            logger.info("COMMAND:[{}] VALUE:[{}]",((Command) object).getCommand(),((Command) object).getValue());
//            if (((Command) object).getCommand().equals("DIGPORT")) {
//                String clientName = ((Command) object).getValue();
//                byte[] data = zk.getData("/seed/io/udp/hole/client/" + clientName, null, null);
//                String[] clientDesc = new String(data).split("\\|");
//                byte[] serverdata = zk.getData("/seed/io/udp/hole/server/" + ioPlugin.getServerName(), null, null);
//                String[] serverDesc = new String(serverdata).split("\\|");
////                P2pClient client = new P2pClient();
//                logger.info("SEND MESSAGE [DIGPORT SUCCESS!!],SERVER[{}],PORT[{}]",clientDesc[0],clientDesc[1]);
////                client.send("DIAPORT SUCCESS!!".getBytes(),clientDesc[0],Integer.valueOf(clientDesc[1]),Integer.valueOf(serverDesc[1]));
//                ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer("DIGPORT SUCCESS!!".getBytes()),new InetSocketAddress(clientDesc[0],Integer.valueOf(clientDesc[1]))));
//            }
//        } else
        if (object instanceof MessageProtoc.Action) {
            //终端
            if (ioPlugin != null && ioPlugin.getMessageCallback() != null)
                ioPlugin.getMessageCallback().callback(((MessageProtoc.Action) object).getType(), ((MessageProtoc.Action) object).getMessageId(), session);
        } else {
            //终端
            ioPlugin.getMessageCallback().otherMessage(object, session);
        }
        if(!session.isWrite()){
            session.reply();
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
