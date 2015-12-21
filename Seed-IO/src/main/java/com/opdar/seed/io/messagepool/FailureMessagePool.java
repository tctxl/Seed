package com.opdar.seed.io.messagepool;

import com.opdar.seed.io.protocol.ClusterProtoc;

import java.util.List;

/**
 * Created by 俊帆 on 2015/12/17.
 */
public interface FailureMessagePool extends MessagePool<ClusterProtoc.Message>{
    List<ClusterProtoc.Message> random(int size);
    ClusterProtoc.Message random();
}
