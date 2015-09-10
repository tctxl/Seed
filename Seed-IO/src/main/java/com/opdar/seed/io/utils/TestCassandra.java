package com.opdar.seed.io.utils;

import org.apache.cassandra.thrift.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/9/9.
 */
public class TestCassandra {
    public static void main(String[] args) throws Exception {
        CassandraConnectionPool pool = new CassandraConnectionPool(1);
        CassandraConnection conn1 = pool.getConn();
        Cassandra.Client client1 = conn1.getClient();
        pool.releaseConn(conn1);
    }


}
