package com.opdar.seed.io.messagepool;

import com.google.protobuf.InvalidProtocolBufferException;
import com.opdar.seed.io.protocol.OnlineProtoc;
import com.opdar.seed.io.utils.CacheUtils;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class SSDBOnlinePool implements MessagePool<OnlineProtoc.Online> {

    private SSDBOnlinePool(){}

    private static SSDBOnlinePool pool = null;

    private static final String HASH_KEY = "ONLINE_POOL";

    public synchronized static SSDBOnlinePool getInstance(){
        if(pool == null)pool = new SSDBOnlinePool();
        return pool;
    }

    @Override
    public boolean set(OnlineProtoc.Online online) {
        CacheUtils.hashCache(HASH_KEY,online.getUserId(), online.toByteArray());
        return true;
    }

    @Override
    public OnlineProtoc.Online get(String userId) {
        try {
            return OnlineProtoc.Online.parseFrom(CacheUtils.hashGet(HASH_KEY, userId).datas.get(0));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean del(String id) {
        return CacheUtils.hashDel(HASH_KEY,id);
    }
}
