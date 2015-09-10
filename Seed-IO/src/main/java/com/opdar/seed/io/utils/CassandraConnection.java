package com.opdar.seed.io.utils;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by 俊帆 on 2015/9/9.
 */
public class CassandraConnection {
    private Cassandra.Client client = null;
    private TTransport tr = null;
    private String host = null;
    public final static String HOST = "192.168.1.240";


    public CassandraConnection(String host) throws TTransportException {
        this.host = host;
        this.connect(host);
    }

    private synchronized void connect(String host) throws TTransportException {
        tr = new TSocket(host, 9042);
        TProtocol proto = new TBinaryProtocol(tr);
        client = new Cassandra.Client(proto);
        tr.open();
    }

    public synchronized void close() {
        if (tr != null && tr.isOpen())
            tr.close();
    }

    public Cassandra.Client getClient() {
        return client;
    }

    public String getHost() {
        return host;
    }
}
