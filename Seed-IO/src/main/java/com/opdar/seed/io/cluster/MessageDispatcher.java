package com.opdar.seed.io.cluster;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.base.Dispatcher;
import com.opdar.seed.io.p2p.P2PClusterPool;
import com.opdar.seed.io.p2p.P2pClient;
import com.opdar.seed.io.protocol.ActionProtocol;
import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.protocol.OnlineProtoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by 俊帆 on 2015/12/17.
 */
public class MessageDispatcher implements Dispatcher<ClusterProtoc.Message,Void> {
    private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
    private static final Logger failureLogger = LoggerFactory.getLogger("MSG_ERROR");
    private com.opdar.seed.io.IOPlugin IOPlugin;

    public MessageDispatcher(com.opdar.seed.io.IOPlugin IOPlugin) {
        this.IOPlugin = IOPlugin;
    }

    @Override
    public Void doIt(ClusterProtoc.Message message) {
        String messageId = message.getMessageId();
        String to = message.getTo();

        //
        byte[] notifyMsg = ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.MSG).setMessageId(messageId).build());

        OnlineProtoc.Online online = IOPlugin.getOnlinePool().get(to);
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
        return null;
    }


    @Override
    public String getType() {
        return ClusterProtoc.Message.Act.MESSAGE.name();
    }

}
