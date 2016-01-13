package com.opdar.seed.io.cluster;

import com.google.protobuf.ByteString;
import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.base.Dispatcher;
import com.opdar.seed.io.p2p.P2PClusterPool;
import com.opdar.seed.io.p2p.P2pClient;
import com.opdar.seed.io.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by 俊帆 on 2015/12/17.
 */
public class MessageDispatcher implements Dispatcher<ClusterProtoc.Message,MethodProtoc.Response> {
    private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
    private static final Logger failureLogger = LoggerFactory.getLogger("MSG_ERROR");
    private com.opdar.seed.io.IOPlugin IOPlugin;

    public MessageDispatcher(com.opdar.seed.io.IOPlugin IOPlugin) {
        this.IOPlugin = IOPlugin;
    }

    @Override
    public MethodProtoc.Response doIt(ClusterProtoc.Message message) {
        try{
            if(message == null)return null;
            String messageId = message.getMessageId();
            String to = message.getTo();

            //
            byte[] notifyMsg = ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.MSG).setMessageId(messageId).build());

            MethodProtoc.Response.Builder builder = MethodProtoc.Response.newBuilder().setCode(0).setType("normal").setContent(ByteString.EMPTY);

            OnlineProtoc.Online online = IOPlugin.getOnlinePool().get(to);
            //如果对方不在线，则消息不发送
            if(online == null){
                //删除该消息
                IOPlugin.getMsgPool().del(messageId);
                return builder.build();
            }
            P2pClient client = P2PClusterPool.get(online.getServerName());
            client.setTimeOut(5000);
            int retries = 3;
            String ret = null;
            while (retries != 0){
                try{
                    client.send(notifyMsg);
                    ret = client.receive();
                    break;
                }catch (Exception e){
                    retries--;
                    logger.info("retries time = {}",retries);
                }
            }
            if(ret == null || !ret.equals("SUCCESS")){
                //Failure
                if(IOPlugin.getMsgFailurePool() != null){
                    boolean ret2 = false;
                    int saveRetries = 3;
                    while (saveRetries != 0){
                        ret2 = IOPlugin.getMsgPool().del(messageId);
                        if(ret2)break;
                        saveRetries--;
                    }
                    if(ret2){
                        ret2 = IOPlugin.getMsgFailurePool().set(message);
                        if(!ret2){
                            ret2 = IOPlugin.getMsgPool().set(message);
                            builder.setCode(1).build();
                            if(!ret2){
                                failureLogger.info("Cache Failure Message:[{}]",messageId);
                            }
                        }
                    }else{
                        failureLogger.info("Cache Failure Message:[{}]",messageId);
                    }
                }
            }else{
                boolean ret2 = IOPlugin.getMsgPool().del(messageId);

                if(!ret2){
                    failureLogger.info("Cache Delete Message:[{}]",messageId);
                }
            }
            return builder.build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getType() {
        return ClusterProtoc.Message.Act.MESSAGE.name();
    }

}
