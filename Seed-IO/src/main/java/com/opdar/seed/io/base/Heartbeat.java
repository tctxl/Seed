package com.opdar.seed.io.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2016/1/7.
 */
public abstract class Heartbeat implements Runnable {

    protected final IoSession session;
    private final static Logger logger = LoggerFactory.getLogger("Heartbeat");

    public Heartbeat(IoSession session) {
        this.session = session;
        logger.info("Start heartbeat!");
    }

    public int overtime = 300;
    private long heartTime = 0;
    private boolean isOverTime = false;


    public void clearHeartbeat() {
        heartTime = System.currentTimeMillis();
        isOverTime = false;
    }

    public void start(){
        if(heartTime == 0){
            session.getContext().executor().schedule(this,overtime,TimeUnit.SECONDS);
        }else{
            clearHeartbeat();
        }
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    @Override
    public void run() {
        if (!session.getContext().channel().isOpen()) {
            return;
        }
        long sec = (System.currentTimeMillis() - heartTime) / 1000;
        if (sec < 0) sec = overtime;
        if (sec < overtime) {
            sec = overtime - sec;
        } else {
            sec = overtime;
            if (isOverTime) {
                //超时
                logger.info("session {} overtime!",session.getId());
                overtime();
                session.downline();
                return;
            } else {
                //正常
                heartbeat();
                isOverTime = true;
            }
        }
        session.getContext().executor().schedule(this, sec, TimeUnit.SECONDS);
    }

    public abstract void overtime();

    public abstract void heartbeat();
}
