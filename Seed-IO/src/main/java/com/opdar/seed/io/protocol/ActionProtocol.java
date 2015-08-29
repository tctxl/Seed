package com.opdar.seed.io.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import sun.misc.BASE64Decoder;

import java.io.IOException;

/**
 * 36进制解码
 * Created by 俊帆 on 2015/8/28.
 */
public class ActionProtocol implements Protocol {

    @Override
    public <A> A execute(byte[] buf) {
        try {
            if(buf != null){
                MessageProtoc.ActionBean actionBean = MessageProtoc.ActionBean.parseFrom(new BASE64Decoder().decodeBuffer(new String(buf)));
                return (A) actionBean;
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
