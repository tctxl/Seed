package com.opdar.framework.web.common;

import com.opdar.framework.aop.base.ClassBean;

import java.util.List;

/**
 * Created by Jeffrey on 2015/4/11.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedRouter {
    private String routerName;
    private ClassBean.MethodInfo methodInfo;
    private ClassBean classBean;
    private ClassBean.MethodInfo.LocalVar requestBodyVar;
    private boolean hasRequestBody;
    private List<String> restfulPar;

    public ClassBean.MethodInfo.LocalVar getRequestBodyVar() {
        return requestBodyVar;
    }

    public boolean hasRequestBody() {
        return hasRequestBody;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public ClassBean.MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(ClassBean.MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public ClassBean getClassBean() {
        return classBean;
    }

    public void setClassBean(ClassBean classBean) {
        this.classBean = classBean;
    }

    public void setRequestBody(boolean hasRequestBody, ClassBean.MethodInfo.LocalVar var) {
        this.hasRequestBody = hasRequestBody;
        this.requestBodyVar = var;
    }

    public void setRestfulPar(List<String> restfulPar) {
        this.restfulPar = restfulPar;
    }

    public List<String> getRestfulPar() {
        return restfulPar;
    }
}
