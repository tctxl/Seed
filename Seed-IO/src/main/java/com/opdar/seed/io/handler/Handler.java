package com.opdar.seed.io.handler;

import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;
import com.opdar.framework.web.parser.HttpParser;
import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.base.Callback;
import com.opdar.seed.io.base.IoSession;
import com.opdar.seed.io.base.Result;
import com.opdar.seed.io.cluster.ClusterClient;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.protocol.MethodProtoc;
import com.opdar.seed.io.protocol.MethodProtocol;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@Sharable
public class Handler extends SimpleChannelInboundHandler<Result> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    public static AttributeKey<IoSession> SESSION_FLAG = AttributeKey.valueOf("session");
    private SeedWeb web;
    private com.opdar.seed.io.IOPlugin IOPlugin;
    private Callback messageCallback;

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
        IoSession session = ctx.attr(SESSION_FLAG).get();
        if(IOPlugin!= null && IOPlugin.getSessionStateCallback() != null)
            IOPlugin.getSessionStateCallback().unregist(session);
        session.downline();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        IoSession session = null;
        ctx.attr(SESSION_FLAG).set(session = new IoSession(ctx));
        if(IOPlugin!= null && IOPlugin.getSessionStateCallback() != null)
            IOPlugin.getSessionStateCallback().regist(session);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Result result) throws Exception {
        IoSession session = ctx.attr(SESSION_FLAG).get();
        Object object = result.get();
        if (object instanceof MessageProtoc.Action) {
            if(((MessageProtoc.Action) object).getType() == MessageProtoc.Action.Type.INQUEUE){
                if(messageCallback != null)messageCallback.call(object);
            }else if (IOPlugin!= null && IOPlugin.getMessageCallback() != null){
                    IOPlugin.getMessageCallback().callback(((MessageProtoc.Action) object).getType(), ((MessageProtoc.Action) object).getMessageId(),session);
            }
        } else if (object instanceof ClusterProtoc.Message) {
            ReferenceCountUtil.retain(result);
            ctx.fireChannelRead(result);
        } else if (object instanceof MethodProtoc.Method) {
            MethodProtoc.Method method = (MethodProtoc.Method) object;
            final String name = method.getName();
            String type = method.getType();
            MethodProtocol.MethodResponse mr = new MethodProtocol.MethodResponse(session);
            final SeedRequest request = new SeedRequest();
            final SeedResponse response = new SeedResponse(mr);
            if (web != null) {
                HttpParser parser = web.getParser(type);

                Object p = null;
                if (parser != null) {
                    p = parser.execute(method.getParamsBytes().toByteArray());
                }

                if (p != null && p instanceof Map) {
                    request.putValues((Map<String, Object>) p);
                }
                request.setBody(method.getParamsBytes().toByteArray());
                ctx.executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        web.execute(name, request, response);
                    }
                });
            }
        }else{
            IOPlugin.getMessageCallback().otherMessage(object,session);
        }
    }

    public void setSeedWeb(SeedWeb seedWeb) {
        this.web = seedWeb;
    }

    public Handler setIOPlugin(IOPlugin IOPlugin) {
        this.IOPlugin = IOPlugin;
        return this;
    }

    public void setMessageCallback(Callback  messageCallback) {
        this.messageCallback = messageCallback;
    }
}
