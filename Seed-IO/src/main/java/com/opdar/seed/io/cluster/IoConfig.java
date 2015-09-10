package com.opdar.seed.io.cluster;

/**
 * 分布式配置
 * Created by 俊帆 on 2015/8/31.
 */
public class IoConfig {

    private long heartbeatTime = 0;

    //0不限制
    private int maxClient = 0;

    //集群节点
    private ClusterPool clusters = new ClusterPool();

    public IoConfig() {
//        TokenUtil.add()
    }

    public void setHeartbeat(long ms) {
        this.heartbeatTime = ms;
    }

    /**
     * @param maxClient 单机最大用户数
     */
    public void setMaxClient(int maxClient) {
        this.maxClient = maxClient;
    }

}
