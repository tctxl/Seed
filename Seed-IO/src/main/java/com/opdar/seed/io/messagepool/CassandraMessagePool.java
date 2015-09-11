package com.opdar.seed.io.messagepool;

import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.utils.CacheUtils;
import com.opdar.seed.io.utils.CassandraUtils;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class CassandraMessagePool implements MessagePool<ClusterProtoc.Message> {

    private CassandraMessagePool(){}

    private static CassandraMessagePool pool = null;

    public synchronized static CassandraMessagePool getInstance(){
        if(pool == null)pool = new CassandraMessagePool();
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
