package com.opdar.seed.io.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.framework.utils.Utils;
import sun.misc.BASE64Decoder;

import java.io.IOException;

/**
 * 36进制解码
 * Created by 俊帆 on 2015/8/28.
 */
public class ActionProtocol implements Protocol<MessageProtoc.Action> {

    @Override
    public MessageProtoc.Action execute(byte[] buf) {
        try {
            if(buf != null){
                MessageProtoc.Action actionBean = MessageProtoc.Action.parseFrom(new BASE64Decoder().decodeBuffer(new String(buf)));
                return actionBean;
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] create(MessageProtoc.Action act) {
        byte[] result = act.toByteArray();
        return Utils.byteMerger(convertLen(result.length), result);
    }

    public static byte[] convertLen(int i) {
        int radix = 36;
        byte buf[] = new byte[]{'-', '0', '0', '0', '0'};
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

}
