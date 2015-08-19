package com.opdar.framework.web.common;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.views.DefaultView;
import com.opdar.framework.web.views.ErrorView;

/**
 * Created by 俊帆 on 2015/8/17.
 */
public class ControllerAct {
    private ThreadLocal<SeedExcuteItrf> before = new ThreadLocal<SeedExcuteItrf>();
    private ThreadLocal<SeedExcuteItrf> after = new ThreadLocal<SeedExcuteItrf>();

    public void setBefore(Class before) {
        if (before != null) {
            try {
                if (this.before.get() == null)
                    this.before.set(SeedInvoke.buildObject(before));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setAfter(Class after) {
        if (after != null) {
            try {
                if (this.after.get() == null)
                    this.after.set(SeedInvoke.buildObject(after));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void invokeAfter() {
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
