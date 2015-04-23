package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.exceptions.NoConfigException;
import com.opdar.framework.server.supports.UriUtil;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.common.IResponse;
import com.opdar.framework.web.common.SeedRequest;
import com.opdar.framework.web.common.SeedResponse;
import com.opdar.framework.web.converts.JSONConvert;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by jeffrey on 2015/4/19.
 */
public class SeedServlet extends GenericServlet {

    protected static SeedWeb web = new SeedWeb();

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        final HttpServletResponse res = (HttpServletResponse)servletResponse;
        IResponse session = new IResponse() {
            public void write(byte[] content, String contentType, int responseCode) {
                try {
                    res.setContentType(contentType);
                    res.setStatus(responseCode);
                    res.getOutputStream().write(content, 0, content.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void writeAndFlush(byte[] content, String contentType, int responseCode) {
                write(content,contentType,responseCode);
                flush();
            }

            public void flush() {
                try {
                    res.flushBuffer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        SeedRequest request = new SeedRequest();
        SeedResponse response = new SeedResponse(session);
        try{
            String queryString = req.getQueryString();
            String path = req.getPathInfo();

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
            e.printStackTrace();
            //400
            if(response!=null)
                response.write(HttpResponseCode.CODE_400.getContent().getBytes(),"text/html", HttpResponseCode.CODE_400.getCode());
        } finally{
            if(response!=null && !response.isWrite())
                response.writeSuccess();
        }
    }
}
