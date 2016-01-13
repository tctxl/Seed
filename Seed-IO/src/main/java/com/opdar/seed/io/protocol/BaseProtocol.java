package com.opdar.seed.io.protocol;

import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.token.Token;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public abstract class BaseProtocol<A> implements Protocol<A> {

    protected byte[] token ;

    public BaseProtocol(){
    }

    public BaseProtocol(Token token){
        this(token.getToken());
    }

    public BaseProtocol(byte[] token){
        this.token = token;
    }

    static char[] digits = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
            'o' , 'p' , 'q' , 'r' , 's' , 't' ,
            'u' , 'v' , 'w' , 'x' , 'y' , 'z'
    };

    public static byte[] convertLen(byte[] token,int i) {
        int radix = 36;
        byte buf[] = new byte[]{'0', '0', '0', '0'};
        boolean negative = (i < 0);
        int charPos = 3;
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
        return Utils.byteMerger(token,buf);
    }
}
