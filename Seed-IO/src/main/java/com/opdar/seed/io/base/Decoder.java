package com.opdar.seed.io.base;

import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.protocol.MethodProtoc;
import com.opdar.seed.io.protocol.Protocol;
import com.opdar.seed.io.token.Token;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 俊帆 on 2015/8/27.
 */
@ChannelHandler.Sharable
public class Decoder extends MessageToMessageDecoder<ByteBuf> {
    private Token token = null;
    private boolean HAS_PARSER = false;
    private static int remain = 0;
    private byte[] merge = null;
    private byte[] lengthBytes = null;
    private List<Object> result = new LinkedList<Object>();
    int length = 0;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() != 0) {
            if (!HAS_PARSER) {
                if (lengthBytes == null) {
                    while (byteBuf.isReadable()) {
                        int i = byteBuf.readByte();
                        if (TokenUtil.contains(i)) {
                            token = TokenUtil.get(i);
                            break;
                        }
                    }
                }
                if (token != null) {
                    length = getLength(byteBuf);
                    if (length == -1) break;
                    HAS_PARSER = true;
                    int readableLen = byteBuf.readableBytes();
                    if (length < readableLen) readableLen = length;
                    remain = length - readableLen;
                    byte[] tempBuf = new byte[readableLen];
                    byteBuf.readBytes(tempBuf);

                    if (merge == null) merge = tempBuf;
                    else merge = Utils.byteMerger(merge, tempBuf);
                }
            } else {
                int canread = remain;
                if (canread > byteBuf.readableBytes()) {
                    canread = byteBuf.readableBytes();
                }
                byte[] tempBuf = new byte[canread];
                byteBuf.readBytes(tempBuf);
                merge = Utils.byteMerger(merge, tempBuf);
                remain = remain - tempBuf.length;
            }

            if (remain == 0 && token != null && merge != null) {
                Protocol protocol = token.getProtocol();
                Object content = protocol.execute(merge);
                HAS_PARSER = false;
                merge = null;
                token = null;
                if (content != null) result.add(content);
            }
        }

        if (remain == 0) {
            list.addAll(result);
            result.clear();
        }
    }


    /**
     * 36进制
     *
     * @param buf
     * @return
     */
    private int getLength(ByteBuf buf) {
        if (lengthBytes != null) {
            int i = 4 - lengthBytes.length;
            if (buf.readableBytes() > i) {
                byte[] remain = new byte[i];
                try {
                    buf.readBytes(remain);
                    lengthBytes = Utils.byteMerger(lengthBytes, remain);
                    String header = new String(lengthBytes);
                    return Integer.parseInt(header, 36);
                } finally {
                    lengthBytes = null;
                }
            } else {
                byte[] remain = new byte[buf.readableBytes()];
                lengthBytes = Utils.byteMerger(lengthBytes, remain);
                return -1;
            }
        } else {
            if (buf.readableBytes() > 4) {
                byte[] bytes = new byte[4];
                buf.readBytes(bytes);
                String header = new String(bytes);
                return Integer.parseInt(header, 36);
            } else if (buf.readableBytes() > 0) {
                lengthBytes = new byte[buf.readableBytes()];
                buf.readBytes(lengthBytes);
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 18081);
            MethodProtoc.Method.Builder method = MethodProtoc.Method.newBuilder();
            method.setName("/test/param.run");
            method.setParams("p1=1&p2=2");
            method.setType("application/x-www-form-urlencoded");

            byte[] result = method.build().toByteArray();
            String s = Integer.toString(result.length, 36);
            int k = 4 - s.length();
            StringBuilder len = new StringBuilder();
            len.append("M");
            while (k != 0) {
                k--;
                len.append(0);
            }
            len.append(s);
            socket.getOutputStream().write(Utils.byteMerger(len.toString().getBytes(), result));
            socket.getOutputStream().flush();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
