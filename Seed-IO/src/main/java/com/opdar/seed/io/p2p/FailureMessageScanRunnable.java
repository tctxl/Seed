package com.opdar.seed.io.p2p;

import com.opdar.seed.io.IOPlugin;
import com.opdar.seed.io.cluster.MessageDispatcher;
import com.opdar.seed.io.messagepool.FailureMessagePool;
import com.opdar.seed.io.messagepool.MessagePool;
import com.opdar.seed.io.protocol.ClusterProtoc;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2015/12/17.
 */
public class FailureMessageScanRunnable implements Runnable {
    public static final long OUTTIME = 30;
    public static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private IOPlugin ioPlugin;
    private static final Logger logger = LoggerFactory.getLogger(FailureMessageScanRunnable.class);
    private static final LinkedList<ClusterProtoc.Message> executes = new LinkedList<ClusterProtoc.Message>();
    private Channel channel;
    private static final Object lock = new Object();

    public FailureMessageScanRunnable(IOPlugin ioPlugin, Channel channel) {
        this.ioPlugin = ioPlugin;
        this.channel = channel;
        ExecuteMessage em = new ExecuteMessage(new MessageDispatcher(ioPlugin));
        Thread thread = new Thread(em);
        thread.start();
    }

    static class ExecuteMessage implements Runnable {
        private MessageDispatcher dispatcher;

        public ExecuteMessage(MessageDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public void run() {
            while (true) {
                logger.debug("Em running!!");
                if (executes.size() > 0) {
                    if (!executes.isEmpty()) {
                        ClusterProtoc.Message message = executes.pop();
                        dispatcher.doIt(message);
                    }
                } else {
                    try {
                        synchronized (lock) {
                            logger.debug("Em wait!");
                            lock.wait();
                            logger.debug("Em wake!");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        logger.debug("starting scan the failed message");
        channel.eventLoop().schedule(this, OUTTIME, TIMEUNIT);
        synchronized (executes) {
            MessagePool<ClusterProtoc.Message> pool = ioPlugin.getMsgFailurePool();
            if (pool instanceof FailureMessagePool) {
                List<ClusterProtoc.Message> messages = ((FailureMessagePool) pool).random(50);
                for (int i = 0; i < messages.size(); i++) {
                    ClusterProtoc.Message message = messages.get(i);
                    boolean ret = pool.del(message.getMessageId());
                    if (ret) {
                        executes.add(message);
                    }
                }
                if(executes.size() > 0){
                    synchronized (lock) {
                        lock.notify();
                        logger.debug("Em Notify!");
                    }
                }
            }
        }
    }
}
