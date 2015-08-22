package com.opdar.framework.web.utils;

import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.PrimaryUtil;
import com.opdar.framework.utils.Utils;

import java.util.*;

/**
 * Created by 俊帆 on 2015/8/22.
 */
public class ArgInvoke {

    public static void invokeObjectArgs(Map<String, Object> values, Map<Integer, SeedExcuteItrf> os, String key, ClassBean classBean) {
        try {
            if (values.containsKey(key)) {
                SeedExcuteItrf exe = null;
                if (os.containsKey(classBean.hashCode())) {
                    exe = os.get(classBean.hashCode());
                } else {
                    exe = (SeedExcuteItrf) classBean.getSeedClz().newInstance();
                    os.put(classBean.hashCode(), exe);
                }
                exe.invokeMethod(Utils.setMethodName(key), values.get(key));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void invokeNormalArgs(Map<String, Object> values, Object[] params, HashMap<String, Integer> sorts, String key, ClassBean.MethodInfo.LocalVar var) {
        if (sorts.containsKey(key) && values.containsKey(key))
            params[sorts.get(key)] = PrimaryUtil.cast(values.get(key), var.getType());
    }

    public static void invokeCollectionArgs(Map<String, Object> values, Object[] params, HashMap<String, Integer> sorts, String key, ClassBean.MethodInfo.LocalVar var, ClassLoader classLoader, Class clz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if(!values.containsKey(key))return;
        String className = var.getSignatureTypes().get(0).replace("/", ".");
        Class genicClz = classLoader.loadClass(className);

        if (Number.class.isAssignableFrom(genicClz) || String.class.isAssignableFrom(genicClz)) {
            Collection list = null;
            if (clz.isInterface()) {
                list = new LinkedList();
            } else {
                list = (Collection) clz.newInstance();
            }
            Object res = values.get(key);
            Collection result = null;

            if (!(res instanceof Collection)) {
                result = new LinkedList();
                result.add(res);
            } else {
                result = (Collection) res;
            }
            for (Iterator it2 = result.iterator(); it2.hasNext(); ) {
                Object val = it2.next();
                list.add(PrimaryUtil.cast(val, genicClz));
            }
            params[sorts.get(key)] = list;
            return;
        }
    }
}
