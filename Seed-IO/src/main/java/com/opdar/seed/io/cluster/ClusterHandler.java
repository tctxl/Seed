package com.opdar.seed.io.cluster;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.messagepool.SSDBMessagePool;
import com.opdar.seed.io.p2p.P2PClusterPool;
import com.opdar.seed.io.p2p.P2pClient;
import com.opdar.seed.io.protocol.*;
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
            if (cluster != null) cluster.clearHeartbeat();
            switch (((ClusterProtoc.Message) object).getAct()) {
                case JOIN: {
                    String serverName = ((ClusterProtoc.Message) object).getFrom();
                    ctx.attr(SESSION_FLAG).set(cluster = new Cluster(ctx,serverName));
                    cluster.heartbeat();
                    logger.info("节点({})[{}:{}]加入成功",cluster.getServerName(), cluster.getIp(), cluster.getPort());
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
                    byte[] notifyMsg = ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.MSG).setMessageId(messageId).build());

                    OnlineProtoc.Online online = IOPlugin.getOnlinePool().get(to);
                    if(IOPlugin.isP2P()){
                        P2pClient client = P2PClusterPool.get(online.getServerName());
                        if(client != null){
                            client.send(notifyMsg);
                        }else{
                            logger.debug("【"+online.getServerName()+"】 服务已丢失");
                        }
                    }else{
                        Cluster toCluster = ClusterPool.get(online.getServerName());
                        toCluster.write(notifyMsg);
                        //告知服务消息已送至队列
                        if (cluster != null) {
                            cluster.write(ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.INQUEUE).setMessageId(messageId).build()));
                        }
                    }
                }
                break;
                case REPLY_HEARTBEAT: {
                    //收到客户端响应
                    cluster.clearHeartbeat();
                }
                break;
                case HEARTBEAT: {
                    //客户端接收到心跳,对服务端响应
                    ctx.write(ClusterProtocol.create(ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.REPLY_HEARTBEAT).build()));
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
