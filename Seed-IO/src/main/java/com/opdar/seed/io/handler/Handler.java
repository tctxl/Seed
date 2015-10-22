package com.opdar.seed.io.handler;

import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;
import com.opdar.framework.web.parser.HttpParser;
import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.base.IoSession;
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
public class Handler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    public static AttributeKey<IoSession> SESSION_FLAG = AttributeKey.valueOf("session");
    private SeedWeb web;
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
        IoSession session = ctx.attr(SESSION_FLAG).get();
        IOPlugin.getSessionStateCallback().unregist(session);
        session.downline();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        IoSession session = null;
        ctx.attr(SESSION_FLAG).set(session =new IoSession(ctx));
        IOPlugin.getSessionStateCallback().regist(session);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        IoSession session = ctx.attr(SESSION_FLAG).get();
        if (object instanceof MessageProtoc.Action) {
            if (IOPlugin.getMessageCallback() != null)
                IOPlugin.getMessageCallback().callback(((MessageProtoc.Action) object).getType(), ((MessageProtoc.Action) object).getMessageId(),session);
        } else if (object instanceof ClusterProtoc.Message) {
            ReferenceCountUtil.retain(object);
            ctx.fireChannelRead(object);
        } else if (object instanceof MethodProtoc.Method) {
            MethodProtoc.Method method = (MethodProtoc.Method) object;
            final String name = method.getName();
            String type = method.getType();
            MethodProtocol.MethodResponse mr = new MethodProtocol.MethodResponse(session);
            final SeedRequest request = new SeedRequest();
            final SeedResponse response = new SeedResponse(mr);
            if (web != null) {
                HttpParser parser = web.getParser(type);

                Object result = null;
                if (parser != null) {
                    result = parser.execute(method.getParamsBytes().toByteArray());
                }

                if (result != null && result instanceof Map) {
                    request.putValues((Map<String, Object>) result);
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
}
