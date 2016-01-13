package com.opdar.seed.io.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.token.Token;

/**
 * Created by 俊帆 on 2015/8/31.
 */
public class ClusterProtocol extends BaseProtocol<ClusterProtoc.Message> {
    public ClusterProtocol(Token token) {
        super(token);
    }

    @Override
    public ClusterProtoc.Message execute(byte[] buf) {
        if (buf != null) {
            try {
                ClusterProtoc.Message clusterMsg = ClusterProtoc.Message.parseFrom(buf);
                return clusterMsg;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] create(ClusterProtoc.Message message) {
        byte[] result = message.toByteArray();
        return Utils.byteMerger(convertLen(result.length), result);
    }

    public static byte[] convertLen(int i) {
        int radix = 36;
        byte buf[] = new byte[]{'c', '0', '0', '0', '0'};
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
