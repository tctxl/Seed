package com.opdar.seed.io.p2p;

import com.opdar.seed.io.protocol.ActionProtocol;
import com.opdar.seed.io.protocol.MessageProtoc;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class P2pClient {
    private String host;
    private Integer port;
    private String name;
    private Callback callback;

    public String getHost() {
        return host;
    }

    public P2pClient setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public P2pClient setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getName() {
        return name;
    }

    public P2pClient setName(String name) {
        this.name = name;
        return this;
    }

    public interface Callback {
        void back();
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public P2pClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public P2pClient() {
    }

    public void send(byte[] data) {
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
            socket.send(packet);
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        P2pClient client = new P2pClient("localhost", 1080);
        String messageId = UUID.randomUUID().toString();
        client.send(ActionProtocol.create(MessageProtoc.Action.newBuilder().setType(MessageProtoc.Action.Type.MSG).setMessageId(messageId).build()));
    }
}
