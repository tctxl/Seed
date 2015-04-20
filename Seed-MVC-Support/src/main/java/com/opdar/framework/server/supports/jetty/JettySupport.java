package com.opdar.framework.server.supports.jetty;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.servlet.ServletSupport;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import java.util.EnumSet;

/**
 * Created by 俊帆 on 2015/4/18.
 */
public class JettySupport extends ServletSupport {
    int port;
    public JettySupport(int port) {
        this.port = port;
    }

    @Override
    public ISupport start() {
        String p = JettySupport.config.get(IConfig.PORT);
        if(p != null)
            port = Integer.valueOf(p);
        Server server = new Server(Integer.parseInt(p));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        EnumSet<DispatcherType> types = EnumSet.allOf(DispatcherType.class);
        context.addServlet(SeedServlet.class, "/*");
//        context.addFilter(ServletSupport.class,"/*", types);
        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.start();
    }

}
