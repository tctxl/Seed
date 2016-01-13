package com.opdar.seed.io.protocol;

import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.token.Token;

/**
 * 36进制解码
 * Created by 俊帆 on 2015/8/28.
 */
public class P2pProtocol extends BaseProtocol<Command> {

    public P2pProtocol(Token token) {
        super(token);
    }

    @Override
    public Command execute(byte[] buf) {
        if (buf != null) {
            return new Command(buf);
        }
        return null;
    }

    public static byte[] create(String command,String value) {
        byte[] result = String.format("%s:%s",command,value).getBytes();
        return Utils.byteMerger(convertLen(result.length), result);
    }

    public static byte[] convertLen(int i) {
        int radix = 36;
        byte buf[] = new byte[]{'p', '0', '0', '0', '0'};
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
