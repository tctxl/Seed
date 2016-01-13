package com.opdar.seed.io.protocol;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.common.IResponse;
import com.opdar.seed.io.base.IoSession;
import com.opdar.seed.io.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class MethodProtocol extends BaseProtocol<MethodProtoc.Method> {

    private static final Logger logger = LoggerFactory.getLogger(MethodProtocol.class);

    public MethodProtocol(Token token) {
        super(token);
    }


    public static byte[] createResponse(String name, String params, String type) {
        MethodProtoc.Method.Builder method = MethodProtoc.Method.newBuilder();
        method.setName(name);
        method.setParams(params);
        method.setType(type);

        byte[] result = method.build().toByteArray();
        return Utils.byteMerger(convertLen(result.length), result);
    }

    public static byte[] create(String name, String params, String type) {
        MethodProtoc.Method.Builder method = MethodProtoc.Method.newBuilder();
        method.setName(name);
        method.setParams(params);
        method.setType(type);

        byte[] result = method.build().toByteArray();
        return Utils.byteMerger(convertLen(result.length), result);
    }

    public static byte[] convertLen(int i) {
        int radix = 36;
        byte buf[] = new byte[]{'M', '0', '0', '0', '0'};
        boolean negative = (i < 0);
        int charPos = 4;
        if (!negative) {
            i = -i;
        }
        while (i <= -radix) {
            buf[charPos--] = (byte) digits[-(i % radix)];
            i = i / radix;
        }
        buf[charPos] = (byte) digits[-i];

        if (negative) {
            buf[--charPos] = '-';
        }
        return buf;
    }

    @Override
    public MethodProtoc.Method execute(byte[] buf) {
        if (buf != null) {
            try {
                MethodProtoc.Method method = MethodProtoc.Method.parseFrom(buf);
                return method;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class MethodResponse implements IResponse {

        private IoSession session;

        public MethodResponse(IoSession session) {
            this.session = session;
        }

        @Override
        public void write(byte[] content, String contentType, int responseCode) {
            MethodProtoc.Response.Builder response = MethodProtoc.Response.newBuilder();
            response.setContent(ByteString.copyFrom(content));
            response.setType(contentType);
            response.setCode(responseCode);
            byte[] data = response.build().toByteArray();
//            [project,version,length,data]
            byte[] project = new byte[]{'-','m','e','t','h','o','d','-'};
            byte[] version = new byte[]{'0','1','0','0'};
            byte[] length = convertLen(data.length);
            byte[] result = new byte[data.length+project.length+version.length+length.length-1];
            System.arraycopy(project,0,result,0,project.length);
            System.arraycopy(version,0,result,project.length,version.length);
            System.arraycopy(length,1,result,project.length+version.length,length.length-1);
            System.arraycopy(data,0,result,project.length+version.length+length.length-1,data.length);
            session.write(result);
        }

        @Override
        public void writeAndFlush(byte[] content, String contentType, int responseCode) {
            MethodProtoc.Response.Builder response = MethodProtoc.Response.newBuilder();
            response.setContent(ByteString.copyFrom(content));
            response.setType(contentType);
            response.setCode(responseCode);
            byte[] data = response.build().toByteArray();
//            [project,version,length,data]
            byte[] project = new byte[]{'-','m','e','t','h','o','d','-'};
            byte[] version = new byte[]{'0','1','0','0'};
            byte[] length = convertLen(data.length);
            byte[] result = new byte[data.length+project.length+version.length+length.length-1];
            System.arraycopy(project,0,result,0,project.length);
            System.arraycopy(version,0,result,project.length,version.length);
            System.arraycopy(length,1,result,project.length+version.length,length.length-1);
            System.arraycopy(data,0,result,project.length+version.length+length.length-1,data.length);
            session.writeAndFlush(result);
        }

        public static void main(String[] args) {

            byte[] project1 = new byte[]{'-','m','e','t','h','o','d','-'};
            byte[] project2 = new byte[]{'-','m','e','t','h','o','d','-'};
            System.out.println(Arrays.equals(project1,project2));
        }

        @Override
        public void flush() {
            session.flush();
        }

        @Override
        public void setHeader(String key, String value) {

        }

        @Override
        public void setHeaders(Map<String, String> headers) {

        }

        @Override
        public void addCookie(String key, String value) {

        }
    }

}
