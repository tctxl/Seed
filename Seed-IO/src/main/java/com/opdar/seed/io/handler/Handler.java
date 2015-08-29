package com.opdar.seed.io.handler;

import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;
import com.opdar.framework.web.parser.HttpParser;
import com.opdar.seed.io.base.IoSession;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.protocol.MethodProtoc;
import com.opdar.seed.io.protocol.MethodProtocol;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@Sharable
public class Handler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    public static AttributeKey<IoSession> SESSION_FLAG = AttributeKey.valueOf("session");
    private SeedWeb web;

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
//        session.downline();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        IdleStateEvent ise = (IdleStateEvent) evt;
//        SheySession session = ctx.attr(SESSION_FLAG).get();
//        if (ise.state() == IdleState.WRITER_IDLE) {
//            session.setHeartBeat(new HeartBeat());
//            IhygeiaProtocol ihygeiaProtocol = IhygeiaProtocol.createProtocol(Constants.Type.HEARTBEAT, session.getHeartBeat().toString());
//            session.write(ihygeiaProtocol);
//        }
//
//        if (ise.state() == IdleState.READER_IDLE) {
//            if (session.getHeartBeat() != null && session.getHeartBeat().overtime()) {
//                session.downline();
//            }
//        }
//
//        if (ise.state() == IdleState.ALL_IDLE) {
//            session.downline();
//        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.attr(SESSION_FLAG).set(new IoSession(ctx));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {
        IoSession session = ctx.attr(SESSION_FLAG).get();
        if (object instanceof MessageProtoc.ActionBean) {
            System.out.println(object);
        }
        if (object instanceof MethodProtoc.Method) {
            MethodProtoc.Method method = (MethodProtoc.Method) object;
            String name = method.getName();
            String type = method.getType();
            MethodProtocol.MethodResponse mr = new MethodProtocol.MethodResponse(session);

            SeedRequest request = new SeedRequest();
            SeedResponse response = new SeedResponse(mr);
            if(web != null){
                HttpParser parser = web.getParser(type);

                Object result = null;
                if(parser != null){
                    result = parser.execute(method.getParamsBytes().toByteArray());
                }

                if(result!= null && result instanceof Map) {
                    request.putValues((Map<String, Object>) result);
                }
                request.setBody(method.getParamsBytes().toByteArray());
                web.execute(name, request, response);
            }
        }
    }

    public void setSeedWeb(SeedWeb seedWeb) {
        this.web = seedWeb;
    }
}
