package com.opdar.framework.server.supports.jetty;

import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.servlet.ServletSupport;
import org.eclipse.jetty.server.Server;

/**
 * Created by 俊帆 on 2015/4/18.
 */
public class JettySupport extends ServletSupport {
    @Override
    public ISupport start() {
        Server server = new Server(8080);
        server.setHandler(new SeedJettyHandler());
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.start();
    }

    public static void main(String[] args) {
        new JettySupport().start();
    }
}
