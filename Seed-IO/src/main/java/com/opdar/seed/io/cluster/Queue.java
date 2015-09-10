package com.opdar.seed.io.cluster;

/**
 * 执行队列
 * Created by 俊帆 on 2015/8/31.
 */
public class Queue {

    //空闲时间
    private long idleTime;

    /**
     * 当队列阻塞一定时间后，队列末消息开始转移处理队列
     */
    public void transfer(){

    }

    /**
     * 当发送心跳包的时候，一并发送队列空闲时间
     * @return
     */
    public long getIdleTime() {
        return idleTime;
    }

}
