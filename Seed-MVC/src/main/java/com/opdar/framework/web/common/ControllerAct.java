package com.opdar.framework.web.common;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.CloseCallback;
import com.opdar.framework.utils.ThreadLocalUtils;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.views.DefaultView;
import com.opdar.framework.web.views.ErrorView;

/**
 * Created by 俊帆 on 2015/8/17.
 */
public class ControllerAct {
    private ThreadLocal<SeedExcuteItrf> before = new ThreadLocal<SeedExcuteItrf>();
    private ThreadLocal<SeedExcuteItrf> after = new ThreadLocal<SeedExcuteItrf>();
    Class<?> beforeClz = null,afterClz = null;
    private static final String CONTROLLER_KEY = "CONTROLLER_KEY";

    public ControllerAct() {
        ThreadLocalUtils.addCloseCallback(new CloseCallback() {
            @Override
            public void close() {
                ThreadLocalUtils.clearThreadLocals(CONTROLLER_KEY,after);
                ThreadLocalUtils.clearThreadLocals(CONTROLLER_KEY,before);
            }
        });
    }

    public void setBefore(Class before) {
        beforeClz = before;
    }

    public void setAfter(Class after) {
        afterClz = after;
    }

    public void invokeAfter() {
        if (afterClz != null) {
            try {
                if (this.after.get() == null){
                    ThreadLocalUtils.record(CONTROLLER_KEY);
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
                    ThreadLocalUtils.record(CONTROLLER_KEY);
                    this.before.set(SeedInvoke.buildObject(beforeClz));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (before.get() != null) {
            try {
                ret = before.get().invokeMethod("before");
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
