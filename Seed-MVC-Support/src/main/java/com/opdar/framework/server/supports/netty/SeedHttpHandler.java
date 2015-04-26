package com.opdar.framework.server.supports.netty;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.supports.UriUtil;
import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;
import com.opdar.framework.web.converts.JSONConvert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.AttributeKey;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by Shey on 14-9-25.
 */
@ChannelHandler.Sharable
public class SeedHttpHandler extends SimpleChannelInboundHandler<Object> {

    public static AttributeKey<SeedSession> SESSION_FLAG = AttributeKey.valueOf("session");
    private static SeedWeb web = new SeedWeb();

    public SeedHttpHandler(){
        web.scanController(NettySupport.config.get(IConfig.CONTROLLER_PATH));
        web.setWebHtml(NettySupport.config.get(IConfig.PAGES));
        web.setWebPublic(NettySupport.config.get(IConfig.PUBLIC));
        web.setDefaultPages(NettySupport.config.get(IConfig.DEFAULT_PAGES));
        web.setHttpConvert(JSONConvert.class);
    }

    private void executePostValues(SeedRequest request,HttpPostRequestDecoder decoder){
        List<InterfaceHttpData> params = decoder.getBodyHttpDatas();
        for(InterfaceHttpData data :params){
            MixedAttribute attribute = (MixedAttribute) data;
            try {
                request.setValue(new String(attribute.getName()), URLDecoder.decode(new String(attribute.content().array()),"utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            SeedRequest request = new SeedRequest();
            SeedSession session = ctx.attr(SESSION_FLAG).get();
            SeedResponse response = new SeedResponse(session);
            try{
                if(req instanceof FullHttpRequest){
                    if(req.getMethod().equals(HttpMethod.POST)){
                        ByteBuf buf = ((FullHttpRequest) req).content();
                        byte[] data = buf.array();
                        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
                        executePostValues(request,decoder);
                        request.setBody(data);
                    }
                    URI uri = new URI(req.getUri());
                    for(Map.Entry<String, String> header:req.headers().entries()){
                        request.setHeader(header.getKey(),header.getValue());
                    }
                    request.setMethod(req.getMethod().name());
                    UriUtil.executeUri(request, uri.getQuery());
                    web.execute(uri.getPath(),request,response);
                }
            }catch (Exception e){
                //400
                if(response!=null) {
                    response.write("response code 400".getBytes(), "text/html", HttpResponseCode.CODE_400.getCode());
                    response.flush();
                }
            } finally{
//                if(response!=null && !response.isWrite())
//                    response.writeSuccess();
            }
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        SeedSession session = new SeedSession(ctx);
        ctx.attr(SESSION_FLAG).set(session);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.attr(SESSION_FLAG).remove();
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}