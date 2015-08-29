package com.opdar.seed.io.protocol;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.framework.web.common.IResponse;
import com.opdar.seed.io.base.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class MethodProtocol implements Protocol {

    private static final Logger logger = LoggerFactory.getLogger(MethodProtocol.class);

    public MethodProtocol() {
    }

    @Override
    public <A> A execute(byte[] buf) {
        if (buf != null) {
            try {
                MethodProtoc.Method method = MethodProtoc.Method.parseFrom(buf);
                return (A) method;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new MethodProtocol().execute("test\nk=1&a=2\napplication/json".getBytes());
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
            session.write(response.build().toByteArray());
        }

        @Override
        public void writeAndFlush(byte[] content, String contentType, int responseCode) {
            MethodProtoc.Response.Builder response = MethodProtoc.Response.newBuilder();
            response.setContent(ByteString.copyFrom(content));
            response.setType(contentType);
            response.setCode(responseCode);
            session.writeAndFlush(response.build().toByteArray());
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
