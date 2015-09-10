package com.opdar.seed.io.p2p;

import com.opdar.seed.io.protocol.ClusterProtoc;
import com.opdar.seed.io.protocol.ClusterProtocol;
import com.opdar.seed.io.protocol.MethodProtoc;
import com.opdar.seed.io.protocol.MethodProtocol;

import java.io.IOException;
import java.net.*;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public class P2pClient {


    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = ClusterProtocol.create(ClusterProtoc.Message.newBuilder().setAct(ClusterProtoc.Message.Act.JOIN).build());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), 1080);
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

}
