package com.opdar.framework.web.common;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface ICookie {

    void setComment(String purpose);

    boolean getSecure();

    void setPath(String uri);

    void setValue(String newValue);

    String getValue();

    int getMaxAge();

    String getComment();

    void setHttpOnly(boolean isHttpOnly);

    String getPath();

    void setMaxAge(int expiry);

    void setDomain(String domain);

    int getVersion();

    void setSecure(boolean flag);

    String getName();

    boolean isHttpOnly();

    String getDomain();

    public void setVersion(int v);
}
