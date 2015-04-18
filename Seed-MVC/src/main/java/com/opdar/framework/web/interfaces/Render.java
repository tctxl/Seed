package com.opdar.framework.web.interfaces;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */

public interface Render {

    public void success();

    public void renderText(String str);

    public void renderView(String view) ;

    public void redirect(String view);

    public void renderJavaScript(String javascript);

    public void renderJSON(String json) ;

    public void renderHtml(String html);

    public void setAttribute(String s, Object o);

    public void renderView(String view, HashMap<String, Object> map);
}
