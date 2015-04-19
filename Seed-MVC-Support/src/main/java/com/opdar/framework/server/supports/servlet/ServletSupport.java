package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.server.base.DefaultConfig;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.exceptions.NoConfigException;
import com.opdar.framework.server.supports.DefaultSupport;
import com.opdar.framework.server.supports.UriUtil;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.common.IResponse;
import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;
import com.opdar.framework.web.converts.JSONConvert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by shiju_000 on 2015/4/8.
 */
public class ServletSupport extends DefaultSupport implements javax.servlet.Filter {
    private static SeedWeb web = new SeedWeb();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if(ServletSupport.config == null){
            String config = filterConfig.getInitParameter("config");
            if(config != null){
                try {
                    Class<?> configClass = Class.forName(config);
                    IConfig conf = (IConfig) configClass.newInstance();
                    config(conf);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(ServletSupport.config == null){
            try {
                throw new NoConfigException();
            } catch (NoConfigException e) {
                e.printStackTrace();
            }
        }else{
            web.scanController(ServletSupport.config.get(IConfig.CONTROLLER_PATH));
            web.setWebHtml(ServletSupport.config.get(IConfig.PAGES));
            web.setWebPublic(ServletSupport.config.get(IConfig.PUBLIC));
        }
        web.setHttpConvert(JSONConvert.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        final HttpServletResponse res = (HttpServletResponse)servletResponse;
        if(req.getServletPath().equals("/favicon.ico")){
            filterChain.doFilter(req,res);
            return ;
        }

        IResponse session = new IResponse() {
            public void write(byte[] content, String contentType, int responseCode) {
                try {
                    res.setContentType(contentType);
                    res.setStatus(responseCode);
                    res.getOutputStream().write(content,0,content.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        SeedRequest request = new SeedRequest();
        SeedResponse response = new SeedResponse(session);
        try{
            String queryString = ((HttpServletRequest) servletRequest).getQueryString();
            String path = ((HttpServletRequest) servletRequest).getServletPath();


            if(((HttpServletRequest) servletRequest).getMethod().equals("POST")){
                byte[] data = Utils.is2byte(servletRequest.getInputStream());
                request.setBody(data);
                Enumeration<String> pNames = req.getParameterNames();
                while(pNames.hasMoreElements()){
                    String pName = pNames.nextElement();
                    String pValue = req.getParameter(pName);
                    request.setValue(pName,pValue);
                }
            }
            Enumeration<String> hNames = req.getHeaderNames();
            while (hNames.hasMoreElements()){
                String hName = hNames.nextElement();
                String hValue = req.getHeader(hName);
                request.setHeader(hName,hValue);
            }
            UriUtil.executeUri(request, queryString);
            web.execute(path,request,response);
        }catch (Exception e){
            //400
            if(response!=null)
                response.write("response code 400".getBytes(),"text/html", HttpResponseCode.CODE_400.getCode());
        } finally{
            if(response!=null && !response.isWrite())
                response.writeSuccess();
        }
    }

    @Override
    public void destroy() {
    }

    public ISupport start() {
        return this;
    }
}
