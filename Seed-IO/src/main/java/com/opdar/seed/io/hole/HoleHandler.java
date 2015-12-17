package com.opdar.seed.io.hole;

import com.opdar.seed.io.protocol.P2pProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * Created by 俊帆 on 2015/12/15.
 */

@ChannelHandler.Sharable
public class HoleHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private ZooKeeper zk = null;
    List<String> children;

    public HoleHandler(ZooKeeper zk) {
        this.zk = zk;
        syncChildren();
    }

    private void syncChildren() {
        try {
            children = zk.getChildren("/seed/io/udp/hole/server", new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    syncChildren();
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    enum Commond {
        HOST, DIGPORT, CLIENT_HOST;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        ByteBuf content = datagramPacket.copy().content();
        byte[] result = new byte[content.readableBytes()];
        content.readBytes(result);
        String[] CommandDesc = new String(result).split(":");
        Commond commond = Commond.valueOf(CommandDesc[0]);
        switch (commond) {
            case HOST:
            case CLIENT_HOST:
                String path = "/seed/io/udp/hole/server/";
                if (commond == Commond.CLIENT_HOST) {
                    path = "/seed/io/udp/hole/client/";
                }
                String hostName = CommandDesc[1];
                if (hostName.length() > 0 && hostName.length() < 255) {
                    System.out.println(datagramPacket.sender());
                    String host = datagramPacket.sender().getHostName();
                    int port = datagramPacket.sender().getPort();
                    System.out.println(String.format("%s|%s", host, port));
                    byte[] data = String.format("%s|%s", host, port).getBytes();
                    zk.setData(path + hostName, data, -1);
                    if (commond == Commond.CLIENT_HOST) {
                        channelHandlerContext.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(children.get(new Random().nextInt(children.size())).getBytes()), datagramPacket.sender()));
                    } else {
                        channelHandlerContext.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(HoleMaker.SUCCESS.getBytes()), datagramPacket.sender()));
                    }
                }
                break;
            case DIGPORT: {
                String[] clients = CommandDesc[1].split(",");
                byte[] r = zk.getData("/seed/io/udp/hole/server/" + clients[0], null, null);
                String[] digHost = new String(r).split("\\|");
                String host = digHost[0];
                String port = digHost[1];
                channelHandlerContext.write(new DatagramPacket(Unpooled.wrappedBuffer(P2pProtocol.create("DIGPORT", clients[1])), new InetSocketAddress(host, Integer.valueOf(port))));
                break;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println();;
    }

}
