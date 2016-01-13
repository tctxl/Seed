package com.opdar.seed.io.base;

import com.opdar.seed.io.protocol.MethodProtoc;
import com.opdar.seed.io.protocol.MethodProtocol;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by Shey on 2015/12/29.
 */
public class Client {

    private int timeout = 3000;
    private SocketAddress address;
    private Socket socket = null;

    public Client(String host, Integer port){
        address = new InetSocketAddress(host,port);
    }

    public Socket getSocket(){
        if(socket == null){
            try {
                socket = SocketFactory.getDefault().createSocket();
                if(timeout > 0)socket.setSoTimeout(timeout);
                socket.setKeepAlive(true);
                connect();
            }catch (SocketException e){
                socket = null;
                connect();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

    public MethodProtoc.Response send(String name, String params, String type){
        try {
            Socket socket = getSocket();
            if(socket == null)return null;
            if(socket.isConnected()){
                socket.getOutputStream().write(MethodProtocol.create(name, params, type));
                socket.getOutputStream().flush();
                while (socket.getInputStream().available() >= 0){
                    InputStream in = socket.getInputStream();
                    byte[] project = new byte[]{'-','m','e','t','h','o','d','-'};
                    byte[] version = new byte[]{'0','1','0','0'};
                    byte[] h = new byte[8];
                    byte[] v = new byte[4];
                    byte[] l = new byte[4];
                    in.read(h);
                    in.read(v);
                    if(Arrays.equals(h, project) &&
                            Arrays.equals(v, version)){
                        in.read(l);
                        byte[] bytes = new byte[Integer.parseInt(new String(l),36)];
                        in.read(bytes);
                        return MethodProtoc.Response.parseFrom(bytes);
                    }else{
                        in.skip(in.available());
                    }
                }
            }else{
                throw new SocketException("Socket Disconnected!");
            }
        }catch (SocketException e){
            socket = null;
            connect();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void connect(){
        if(socket != null){
            try {
                if(!socket.isConnected()){
                    socket.connect(address);
                }else{
                    socket.close();
                }
            }catch (SocketException e){
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            getSocket();
        }
    }

    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }
}
