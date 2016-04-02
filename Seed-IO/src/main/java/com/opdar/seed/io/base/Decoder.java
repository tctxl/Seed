package com.opdar.seed.io.base;

import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.protocol.MethodProtocol;
import com.opdar.seed.io.protocol.Protocol;
import com.opdar.seed.io.token.Token;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by 俊帆 on 2015/8/27.
 */
@ChannelHandler.Sharable
public class Decoder extends ChannelInboundHandlerAdapter {

    public Decoder() {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            ByteBuf cast = null;
            SocketAddress address = null;
            if (msg instanceof DatagramPacket) {
                cast = ((DatagramPacket) msg).copy().content();
                address = ((DatagramPacket) msg).sender();
            } else {
                cast = (ByteBuf) msg;
                address = ctx.channel().remoteAddress();
            }
            try {
                decode(ctx, cast, address);
            } finally {
                ReferenceCountUtil.release(cast);
            }
        } catch (DecoderException e) {
            throw e;
        } catch (Exception e) {
            throw new DecoderException(e);
        }
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, SocketAddress address) throws Exception {
        while (byteBuf.readableBytes() != 0) {
            Token token = null;
            byte[] header = new byte[0];
            while (byteBuf.isReadable()) {
                byte b = byteBuf.readByte();
                byte[] _header = Utils.byteMerger(header, new byte[]{b});
                if(TokenUtil.startWith(_header)){
                    header = _header;
                    if (TokenUtil.contains(header)) {
                        token = TokenUtil.get(header);
                        break;
                    }
                }else{
                    byteBuf.markReaderIndex();
                }
            }
            if(token != null){
                byte[] bytes = new byte[4];
                if(byteBuf.readableBytes() < bytes.length){
                    byteBuf.resetReaderIndex();
                    break;
                }
                byteBuf.readBytes(bytes);
                String header0 = new String(bytes);
                int length = Integer.parseInt(header0, 36);
                if(byteBuf.readableBytes() < length){
                    byteBuf.resetReaderIndex();
                    break;
                }
                byte[] data = new byte[length];
                byteBuf.readBytes(data);
                Protocol protocol = token.getProtocol();
                try {
                    Object content = protocol.execute(data);
                    if (content != null) {
                        Result result = new Result(content, address);
                        channelHandlerContext.fireChannelRead(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            Socket socket = new Socket("192.168.1.178", 18081);
            String name = "/test/param.run";
            String params = "p1=1&p2=2";
            String type = "application/x-www-form-urlencoded";
            socket.getOutputStream().write(MethodProtocol.create(name, params, type));
            socket.getOutputStream().flush();
            while (socket.getInputStream().available() >= 0) {
                InputStream in = socket.getInputStream();
                String result = new String(Utils.is2byte(in));
                System.out.println(result);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
