package com.opdar.seed.io.messagepool;

import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.utils.CacheUtils;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class SSDBMessagePool implements MessagePool<ClusterProtoc.Message> {

    private SSDBMessagePool(){}

    private static SSDBMessagePool pool = null;

    public synchronized static SSDBMessagePool getInstance(){
        if(pool == null)pool = new SSDBMessagePool();
        return pool;
    }

    @Override
    public boolean set(ClusterProtoc.Message message) {
        CacheUtils.cache(message.getMessageId(), message.toByteArray(), -1);
        return true;
    }

    @Override
    public ClusterProtoc.Message get(String messageId) {
        try {
            return ClusterProtoc.Message.parseFrom(CacheUtils.getCache(messageId).datas.get(0));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean del(String id) {
        return CacheUtils.del(id).ok();
    }
}
