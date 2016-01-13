package com.opdar.seed.io.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.framework.utils.Utils;
import com.opdar.seed.io.token.Token;

/**
 * 36进制解码
 * Created by 俊帆 on 2015/8/28.
 */
public class ActionProtocol extends BaseProtocol<MessageProtoc.Action> {

    public ActionProtocol(Token token) {
        super(token);
    }

    @Override
    public MessageProtoc.Action execute(byte[] buf) {
        try {
            if(buf != null){
                MessageProtoc.Action actionBean = MessageProtoc.Action.parseFrom(buf);
                return actionBean;
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] create(MessageProtoc.Action act) {
        byte[] result = act.toByteArray();
        return Utils.byteMerger(convertLen(new byte[]{'-'},result.length), result);
    }

}
