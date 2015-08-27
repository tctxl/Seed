package com.opdar.framework.web.common;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.CloseCallback;
import com.opdar.framework.utils.ThreadLocalUtils;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.views.DefaultView;
import com.opdar.framework.web.views.ErrorView;

import java.util.HashMap;
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
    private ThreadLocal<SeedExcuteItrf> before = new ThreadLocal<SeedExcuteItrf>();
    private ThreadLocal<SeedExcuteItrf> after = new ThreadLocal<SeedExcuteItrf>();
    private Class<?> beforeClz = null, afterClz = null;
    private static final String ROUTER_KEY = "ROUTER_KEY";

    private ControllerAct controllerAct;
    private HashMap<String, ClassBean> argMapped = new HashMap<String, ClassBean>();

    public SeedRouter() {
        ThreadLocalUtils.addCloseCallback(new CloseCallback() {
            @Override
            public void close() {
                ThreadLocalUtils.clearThreadLocals(ROUTER_KEY,after);
                ThreadLocalUtils.clearThreadLocals(ROUTER_KEY,before);
            }
        });
    }

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


    public void setBefore(Class before) {
        beforeClz = before;
    }

    public void setAfter(Class after) {
        afterClz = after;
    }

    public void setControllerAct(ControllerAct controllerAct) {
        this.controllerAct = controllerAct;
    }

    public ControllerAct getControllerAct() {
        return controllerAct;
    }

    public void putArgsMapped(String name, ClassBean bean) {
        argMapped.put(name, bean);
    }

    public ClassBean getArgsMapped(String name) {
        return argMapped.get(name);
    }

    public HashMap<String, ClassBean> getArgMapped() {
        return argMapped;
    }

    public boolean containsArgsMapped(String name) {
        return argMapped.containsKey(name);
    }

    public void invokeAfter() {
        if (afterClz != null) {
            try {
                if (this.after.get() == null) {
                    ThreadLocalUtils.record(ROUTER_KEY);
                    this.after.set(SeedInvoke.buildObject(afterClz));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (after.get() != null) {
            try {
                after.get().invokeMethod("after");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object invokeBefore() {
        Object ret = null;

        if (beforeClz != null) {
            try {
                if (this.before.get() == null) {
                    ThreadLocalUtils.record(ROUTER_KEY);
                    this.before.set(SeedInvoke.buildObject(beforeClz));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (before.get() != null) {
            try {
                before.get().invokeMethod("before");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ret != null) {
                    if (ret instanceof Boolean) {
                        if (!(Boolean) ret) {
                            return new ErrorView(HttpResponseCode.CODE_401);
                        }
                    } else if (ret instanceof View) {
                        return (View) ret;
                    } else {
                        return new DefaultView(ret);
                    }
                }
            }
        }
        return ret;
    }
}
