package com.opdar.seed.io.utils;

import org.apache.thrift.transport.TTransportException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2015/9/9.
 */
public class CassandraConnectionPool {

    Semaphore access = null;
    CassandraConnection[] pool = null;
    boolean[] used = null;
    int round = 0;
    int conn_num = 0;

    public CassandraConnectionPool(int conn_num) {
        this.conn_num = conn_num;
        init();
    }

    private void init() {
        access = new Semaphore(conn_num);   //有几个连接就允许有几个线程同时访问连接池。
        pool = new CassandraConnection[conn_num];
        used = new boolean[conn_num];
        for (int i = 0; i < pool.length; i++) {
            try {
                pool[i] = new CassandraConnection(CassandraConnection.HOST);
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
    }

    public CassandraConnection getConn() throws InterruptedException {
        if (access.tryAcquire(3, TimeUnit.SECONDS)) {
            synchronized (this) {
                for (int i = 0; i < pool.length; i++) {
                    if (!used[i]) {
                        used[i] = true;
                        return pool[i];
                    }
                }
            }
        }
        throw new RuntimeException("all client is too busy");
    }

    public void releaseConn(CassandraConnection client) {
        boolean released = false;
        synchronized (this) {
            for (int i = 0; i < pool.length; i++) {
                if (client == pool[i] && used[i]) {
                    used[i] = false;
                    released = true;
                    break;
                }
            }
        }
        if (released)
            access.release();
    }

    public void shutdownPool() {
        if (pool != null) {
            for (int i = 0; i < pool.length; i++){
                pool[i].close();
            }
        }
    }
}
