package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.server.supports.UriUtil;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.*;
import com.opdar.framework.web.parser.HttpParser;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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

            @Override
            public void setHeader(String key, String value) {
                res.setHeader(key,value);
            }

            @Override
            public void setHeaders(Map<String, String> headers) {
                for(Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();it.hasNext();){
                    Map.Entry<String, String> entry = it.next();
                    res.setHeader(entry.getKey(),entry.getValue());
                }
            }
        };
        SeedRequest request = new SeedRequest();
        SeedResponse response = new SeedResponse(session);
        ISession iSession = new ServletSession(req.getSession());
        request.setSession(iSession);
        List<ICookie> iCookies = new LinkedList<ICookie>();
        for(Cookie cookie:req.getCookies()){
            ICookie iCookie = new ServletCookie(cookie);
            iCookies.add(iCookie);
        }
        request.setCookie(iCookies);
        try{
            String queryString = req.getQueryString();
            String path = req.getPathInfo();
            //add headers
            Enumeration<String> hNames = req.getHeaderNames();
            while (hNames.hasMoreElements()){
                String hName = hNames.nextElement();
                String hValue = req.getHeader(hName);
                request.setHeader(hName,hValue);
            }

            if(((HttpServletRequest) servletRequest).getMethod().equals("POST")){
                byte[] data = Utils.is2byte(servletRequest.getInputStream());
                HttpParser parser = web.getParser(request.getContentType());
                Object result = parser.execute(data);
                if(result instanceof Map){
                    request.putValues((Map<String, Object>) result);
                }
                request.setBody(data);
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
