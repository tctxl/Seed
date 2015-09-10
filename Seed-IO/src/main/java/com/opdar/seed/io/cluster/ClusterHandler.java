package com.opdar.seed.io.cluster;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.messagepool.SSDBMessagePool;
import com.opdar.seed.io.protocol.ActionProtocol;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.protocol.OnlineProtoc;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

@Sharable
public class ClusterHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ClusterHandler.class);
    public static AttributeKey<Cluster> SESSION_FLAG = AttributeKey.valueOf("clusters");
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
        Cluster cluster = ctx.attr(SESSION_FLAG).get();
        if (cluster != null) {
            logger.info("节点[{}:{}]退出", cluster.getIp(), cluster.getPort());
            cluster.downline();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {
        Cluster cluster = ctx.attr(SESSION_FLAG).get();
        if (object instanceof ClusterProtoc.Message) {
            switch (((ClusterProtoc.Message) object).getAct()) {
                case JOIN: {
                    ctx.attr(SESSION_FLAG).set(cluster = new Cluster(ctx));
                    logger.info("节点[{}:{}]加入成功", cluster.getIp(), cluster.getPort());
                    cluster.reacher();
                }
                break;
                case REACHER: {
                    Cluster.CONNECTED = 1;
                    InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                    logger.info("成功加入池[{}:{}]", address.getAddress().getCanonicalHostName(), address.getPort());
                }
                break;
                case MESSAGE: {
                    //get a message id
                    String messageId = ((ClusterProtoc.Message) object).getMessageId();
                    //use id to get a message from message pool
                    ClusterProtoc.Message message = IOPlugin.getMsgPool().get(messageId);
                    String to = message.getTo();
                    //
                    OnlineProtoc.Online online = IOPlugin.getOnlinePool().get(to);
                    Cluster toCluster = ClusterPool.get(online.getHost().concat(":").concat(String.valueOf(online.getPort())));
                    toCluster.write(ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.MSG).setMessageId(messageId).build()));

                    //告知服务消息已送至队列
                    cluster.write(ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.INQUEUE).setMessageId(messageId).build()));
                }
                break;
            }
        }
    }

    public static void main(String[] args) {
//        ClusterProtoc.Message message = ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.MESSAGE).setMessageId("1").setTo("127.0.0.1:53036").setFrom("127.0.0.1:53095").build();
//        SSDBMessagePool.getInstance().set(message);
        System.out.println(SSDBMessagePool.getInstance().get("1"));
    }

    public ClusterHandler setIOPlugin(IOPlugin IOPlugin) {
        this.IOPlugin = IOPlugin;
        return this;
    }

    public IOPlugin getIOPlugin() {
        return IOPlugin;
    }
}
