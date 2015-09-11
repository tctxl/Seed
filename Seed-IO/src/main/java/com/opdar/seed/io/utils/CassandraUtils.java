package com.opdar.seed.io.utils;

import org.apache.cassandra.thrift.*;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2015/9/9.
 */
public class CassandraUtils {
    private CassandraConnectionPool pool = new CassandraConnectionPool(1);
    private static CassandraUtils cassandraUtils = null;
    public static CassandraUtils getInstance(){
        if(cassandraUtils == null)cassandraUtils = new CassandraUtils();
        return cassandraUtils;
    }
    public static void main(String[] args) throws Exception {
        CassandraUtils cassandra = CassandraUtils.getInstance();
//        cassandra.setKeySpace("messages");
        cassandra.insert("messages","1","2","333");
        byte[] column = cassandra.get("messages", "1", "2");
        System.out.println(new String(column));
    }

    public CassandraConnection getConnection() throws InterruptedException {
        CassandraConnection conn1 = pool.getConn();
        return conn1;
    }

    public void retain(CassandraConnection conn1){
        pool.releaseConn(conn1);
    }

    public void close(CassandraConnection conn1){
        conn1.close();
    }

    public void setKeySpace(String keyspace) throws Exception {
        CassandraConnection connection = getConnection();
        Cassandra.Client client = connection.getClient();
        client.set_keyspace(keyspace);
        retain(connection);
    }

    public byte[] get(String columnFamily, String key) throws Exception {
        return get(columnFamily,key,key);
    }

    public byte[] get(String columnFamily, String key, String name) throws Exception {
        CassandraConnection connection = getConnection();
        Cassandra.Client client = connection.getClient();
        ColumnPath path = new ColumnPath(columnFamily);
        path.setColumn(ByteBuffer.wrap(name.getBytes("utf-8"))); // 读取id
        ColumnOrSuperColumn column = client.get(ByteBuffer.wrap(key.getBytes("utf-8")),path,ConsistencyLevel.ONE);
        byte[] value = column.getColumn().getValue();
        retain(connection);
        return value;
    }

    public void insert(String columnFamily,String name,String value) throws Exception {
        insert(columnFamily,name,name,value);
    }

    public void insert(String columnFamily,String key,String name,String value) throws Exception {
        CassandraConnection connection = getConnection();
        Cassandra.Client client = connection.getClient();
        ColumnParent parent = new ColumnParent(columnFamily);// column family
        Column nameColumn = new Column(ByteBuffer.wrap(name.getBytes("utf-8")));
        nameColumn.setValue(ByteBuffer.wrap(value.getBytes("utf-8")));
        nameColumn.setTimestamp(System.currentTimeMillis());
        client.insert(ByteBuffer.wrap(key.getBytes("utf-8")), parent, nameColumn, ConsistencyLevel.ONE);
        retain(connection);
    }
}
