package com.opdar.seed.io.cluster;

import com.opdar.seed.io.base.Callback;
import com.opdar.seed.io.protocol.MessageProtoc;
import com.opdar.seed.io.token.ActionToken;
import com.opdar.seed.io.token.ClusterToken;
import com.opdar.seed.io.token.TokenUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2015/9/15.
 */
public class ClusterClient implements Callback<Object, Object> {

    private int AUTO_RECONNECT = 1;
    private Channel ch = null;
    private final ExecutorService es = Executors.newSingleThreadExecutor();
    private final Logger logger = LoggerFactory.getLogger(ClusterClient.class);
    private boolean isStop = false;
    private boolean isConnected = false;
    private static final Object lock = new Object();
    int tries = 3;
    private Object result;
    private boolean ret = false;
    private String host;
    private Integer port;
    private Callback<String,Void> callback;

    public ClusterClient(String host, Integer port) {
        this(host,port,new Callback<String,Void>(){
            @Override
            public Void call(String object) {
                return null;
            }
        });
    }

    public ClusterClient( String host, Integer port,Callback<String, Void> callback) {
        this.callback = callback;
        this.host = host;
        this.port = port;
        connect();
    }

    public void setHostAndPort(String host,Integer port) {
        this.host = host;
        this.port = port;
    }

    public void stop() {
        isStop = true;
        synchronized (lock){
            lock.notify();
        }
        es.shutdown();
        if (ch != null) {
            ch.close();
            ((NioSocketChannel) ch).shutdownOutput();
        }
    }

    public MessageProtoc.Action send(final byte[] data) {
        synchronized (lock) {
            try {
                while (isConnected && tries != 0) {
                    ch.writeAndFlush(data).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            try {
                                logger.debug("msg result : [{}]", future.toString());
                                if (!future.isSuccess()) {
                                    logger.debug("发送失败，正在重试！");
                                    ret = false;
                                }else{
                                    ret = true;
                                    logger.debug("Open :{} ,State: 发送成功！",ch.isOpen());
                                }
                                synchronized (lock) {
                                    lock.notify();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ret) {
                        break;
                    } else {
                        tries--;
                    }
                }
                return (MessageProtoc.Action) result;
            } finally {
                ret = false;
                tries = 3;
                result = null;
            }
        }
    }

    static int i = 0;

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(1);
        final Object lock = new Object();
        int k = 0;
        while (k != 2000) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("locking i:" + i);
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }
            });
            k++;
        }
    }

    public void connect() {
        if (isStop) return;
        ClusterInitializer.getHANDLER().setMessageCallback(this);
        TokenUtil.add(new ActionToken());
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClusterInitializer());
            logger.debug("正在连接服务...");
            ch = b.connect(host, port).sync().channel();
            isConnected = true;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ch != null)
                        ch.closeFuture().sync();
                    isConnected = false;
                    logger.debug("链路已断开!");
                    if(callback != null)callback.call("DISCONNECTED");
                } catch (Exception e) {
                    logger.error(e.toString());
                } finally {
                    group.shutdownGracefully();
                    if (AUTO_RECONNECT == 1 && !isStop) {
                        logger.debug("正在进行重连...");
                        if(callback != null)callback.call("RECONNECTED");
                        connect();
                    }
                }
            }
        });
        TokenUtil.add(new ClusterToken());
        group.schedule(new Runnable() {
            @Override
            public void run() {
                logger.debug("检查连接状态...[{}]", !isConnected ? "失败" : "成功");
                if (AUTO_RECONNECT == 0) {
                    ch.close();
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object call(Object object) {
        synchronized (lock) {
            this.result = object;
            ret = true;
            lock.notify();
        }
        return null;
    }
}
