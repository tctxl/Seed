package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.DefaultSupport;
import org.eclipse.jetty.server.Server;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by shiju_000 on 2015/4/8.
 */
public class ServletSupport extends DefaultSupport implements javax.servlet.Filter {

    public static void main(String[] args) {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
    }

    public ISupport start() {
        return this;
    }
}
