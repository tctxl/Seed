package com.opdar.framework.web.utils;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.asm.Type;
import com.opdar.framework.utils.ParamsUtil;
import com.opdar.framework.utils.ResourceUtils;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.anotations.*;
import com.opdar.framework.web.common.Context;
import com.opdar.framework.web.common.ControllerAct;
import com.opdar.framework.web.common.SeedRouter;

import java.util.*;

/**
 * Created by 俊帆 on 2015/8/17.
 */
public class ControllerInvoke {
//    private static final HashMap<String, ControllerAct> controllerSort = new HashMap<String, ControllerAct>();
    private HashMap<String, SeedRouter> routers = new HashMap<String, SeedRouter>();
    private List<String> restfulList = new LinkedList<String>();

    /**
     * 扫描包下是否有Controller
     */
    public void scan(final String packageName, final String perfix , final ClassLoader loader){
        ResourceUtils.find(new ResourceUtils.FileFinder() {
            @Override
            public String suffix() {
                return perfix == null ? ".class":perfix;
            }

            @Override
            public String getPackageName() {
                return packageName;
            }

            @Override
            public void call(String packageName, String file,String fullName) {
                createController(packageName+file,loader,perfix);
            }
        },loader);
        createRouterArgsMapped(loader);
    }

    public void createController(String className,ClassLoader loader,String perfix){

        try {
            Class<?> c = loader.loadClass(className);
            {
                if (c.isAnonymousClass()) return;
                SeedInvoke.init(c);
                ClassBean classBean = SeedInvoke.getBeanSymbols().get(c);
                Type controllerAfter = null;
                Type controllerBefore = null;
                ControllerAct act = new ControllerAct();
                for (ClassBean.AnotationInfo anotationInfo : classBean.getAnotations()) {
                    boolean isController = isController(anotationInfo);
                    boolean isAfter = isAfter(anotationInfo);
                    boolean isBefore = isBefore(anotationInfo);
                    if (isAfter) {
                        controllerAfter = (Type) anotationInfo.getValue().get(0).getValue();
                        continue;
                    }
                    if (isBefore) {
                        controllerBefore = (Type) anotationInfo.getValue().get(0).getValue();
                        continue;
                    }
                    if (isController) {
                        List<ClassBean.AnotationInfo.AnotationValue> anotations = anotationInfo.getValue();
                        String controllerRouter = "";
                        if (anotations.size() > 0) {
                            ClassBean.AnotationInfo.AnotationValue value = anotations.get(0);
                            controllerRouter = String.valueOf(value.getValue());
                        }
                        String prefixName = "";
                        if (anotations.size() > 1) {
                            ClassBean.AnotationInfo.AnotationValue prefix = anotationInfo.getValue().get(1);
                            if (prefix.getValue().toString().trim().length() > 0) {
                                prefixName = ".".concat(prefix.getValue().toString().toUpperCase());
                            }
                        }
                        List<ClassBean.MethodInfo> methods = classBean.getMethods();

                        scanRouter(perfix, loader, classBean, controllerRouter, prefixName, methods ,act);
                    }
                }
                controllerAop(loader, controllerAfter, controllerBefore, act);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描路由
     * @param perfix
     * @param loader
     * @param classBean
     * @param controllerRouter
     * @param prefixName
     * @param methods
     */
    private void scanRouter(String perfix, ClassLoader loader, ClassBean classBean, String controllerRouter, String prefixName, List<ClassBean.MethodInfo> methods ,ControllerAct act) {
        for (ClassBean.MethodInfo methodInfo : methods) {
            String routerName = null;
            Type after = null, before = null;
            SeedRouter router = null;
            for (ClassBean.AnotationInfo methodAnotation : methodInfo.getAnotations()) {
                boolean isRouter = isRouter(methodAnotation);
                boolean isRouterAfter = isAfter(methodAnotation);
                boolean isRouterBefore = isBefore(methodAnotation);
                if (!isRouter) {
                    continue;
                }
                if (isRouterAfter) {
                    after = (Type) methodAnotation.getValue().get(0).getValue();
                    continue;
                }
                if (isRouterBefore) {
                    before = (Type) methodAnotation.getValue().get(0).getValue();
                    continue;
                }
                routerName = methodInfo.getName();
                if (methodAnotation.getValue().size() > 0) {
                    ClassBean.AnotationInfo.AnotationValue routerValue = methodAnotation.getValue().get(0);
                    if (!routerValue.getValue().equals("")) {
                        routerName = routerValue.getValue().toString();
                    }
                }
                router = createRouter(routerName, perfix, controllerRouter, prefixName, classBean, methodInfo ,act);
            }
            routerAop(loader, router, after, before);
        }
    }

    private SeedRouter createRouter(String routerName, String perfix, String controllerRouter, String prefixName, ClassBean classBean, ClassBean.MethodInfo methodInfo, ControllerAct act){
        SeedRouter seedRouter = new SeedRouter();
        List<String> restfulPar = new ArrayList<String>();
        if (perfix == null) perfix = "";
        else perfix = Utils.testRouter(perfix.concat("/"));
        routerName = Utils.parseSignFactor(routerName, restfulPar).replace(" %s ", ".*");
        String router = perfix.concat(Utils.testRouter(controllerRouter)).concat(Utils.testRouter(routerName));
        String p1 = prefixName;
        seedRouter.setRouterName(routerName.concat(p1));
        if (restfulPar.size() > 0) {
            restfulList.add(router.concat(p1.replace(".", "\\.")).toUpperCase());
            p1 = p1.replace(".", "\\.");
            seedRouter.setRestfulPar(restfulPar);
        }
        seedRouter.setControllerAct(act);
        routers.put(router.toUpperCase().concat(p1), seedRouter);
        seedRouter.setClassBean(classBean);
        seedRouter.setMethodInfo(methodInfo);
        return seedRouter;
    }

    private void routerAop(ClassLoader loader, SeedRouter router, Type after, Type before) {
        if (after != null) {
            try {
                Class clz = loader.loadClass(after.getClassName());
                router.setAfter(clz);
//                routerAfters.put(routerName, clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (before != null) {
            try {
                Class clz = loader.loadClass(before.getClassName());
                router.setBefore(clz);
//                routerBefores.put(routerName, clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void controllerAop(ClassLoader loader, Type controllerAfter, Type controllerBefore,ControllerAct act) {
        if (controllerAfter != null) {
            try {
                Class clz = loader.loadClass(controllerAfter.getClassName());
                act.setAfter(clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (controllerBefore != null) {
            try {
                Class clz = loader.loadClass(controllerBefore.getClassName());
                act.setBefore(clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 创建路由参数映射表
     */
    public void createRouterArgsMapped(ClassLoader loader) {
        for (Iterator<String> it = routers.keySet().iterator(); it.hasNext(); ) {
            String routerName = it.next();
            SeedRouter router = routers.get(routerName);
            for (ClassBean.MethodInfo.LocalVar var : router.getMethodInfo().getLocalVars()) {
                for (ClassBean.AnotationInfo anotation : var.getAnnotations()) {
                    boolean hasRequestBody = isRequestBody(anotation);
                    if (hasRequestBody) {
                        router.setRequestBody(true, var);
                        break;
                    }
                }
                switch (var.getType().getSort()) {
                    case Type.OBJECT:
                        if (!isString(var)) {
                            if (isList(var)) {
                                if (var.getSignatureTypes().size() > 1) {
                                    String className = var.getSignatureTypes().get(var.getSignatureTypes().size() - 2).replace("/", ".");
                                    try {
                                        Class clz = loader.loadClass(className);
                                        if(clz.isAssignableFrom(String.class) || clz.isAssignableFrom(Number.class)){
                                            router.putArgsMapped(var.getName(), null);
                                            break;
                                        }
                                        SeedInvoke.init(clz);
                                        ClassBean classBean = SeedInvoke.getBeanSymbols().get(clz);
                                        for (ClassBean.FieldInfo fieldInfo : classBean.getField()) {
                                            router.putArgsMapped(fieldInfo.getName(), classBean);
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    Class clz = loader.loadClass(var.getType().getClassName());
                                    if(Number.class.isAssignableFrom(clz)){
                                        router.putArgsMapped(var.getName(), null);
                                        continue;
                                    }
                                    SeedInvoke.init(clz);
                                    ClassBean classBean = SeedInvoke.getBeanSymbols().get(clz);
                                    for (ClassBean.FieldInfo fieldInfo : classBean.getField()) {
                                        router.putArgsMapped(fieldInfo.getName(), classBean);

                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            router.putArgsMapped(var.getName(), null);

                        }
                        break;
                    default:
                        router.putArgsMapped(var.getName(), null);
                        break;
                }
            }

        }

    }

    public boolean containsRouter(String routerName){
        return routers.containsKey(routerName.toUpperCase());
    }

    public SeedRouter invokeDefaultRouter(String routerName){
        return routers.get(routerName.toUpperCase());
    }

    public RestfulInvoke invokeRestfulRouter(String routerName){
        RestfulInvoke restful = null;
        HashMap<String, String> restfulResult = new HashMap<String, String>();
        for (String restfulPath : restfulList) {
            if (routerName.matches(restfulPath) && routers.containsKey(restfulPath)) {
                restful = new RestfulInvoke();
                SeedRouter router = routers.get(restfulPath);
                String[] sp = restfulPath.replaceAll("\\.\\*", "&").split("&");
                String tempRouterName = routerName;
                for (String s : sp) {
                    tempRouterName = tempRouterName.replaceAll(s, "&");
                }
                int tempIndex = -1;
                if ((tempIndex = tempRouterName.indexOf("&")) >= 0) {
                    String[] result = tempRouterName.split("&");
                    int plus = tempIndex == 0 ? 1 : 0;
                    for (int i = 0; i < router.getRestfulPar().size(); i++) {
                        String key = router.getRestfulPar().get(i);
                        String value = null;
                        if (i < result.length) {
                            value = result[i + plus];
                        }
                        restfulResult.put(key, value);
                    }
                }
                routerName = restfulPath;

                restful.setRouterName(routerName);
                restful.setRestfulResult(restfulResult);
                restful.setRouter(router);
                break;
            }
        }
        return restful;
    }

    private boolean isString(ClassBean.MethodInfo.LocalVar var) {
        return var.getType().getClassName().equals(String.class.getName());
    }

    private boolean isList(ClassBean.MethodInfo.LocalVar var) {
        return var.getType().getClassName().matches("java.util.*?List");
    }

    private boolean isRouter(ClassBean.AnotationInfo anotationInfo) {
        return anotationInfo.getType().getClassName().equals(Router.class.getName());
    }

    private boolean isBefore(ClassBean.AnotationInfo anotationInfo) {
        return anotationInfo.getType().getClassName().equals(Before.class.getName());
    }

    private boolean isAfter(ClassBean.AnotationInfo anotationInfo) {
        return anotationInfo.getType().getClassName().equals(After.class.getName());
    }

    private boolean isController(ClassBean.AnotationInfo anotationInfo) {
        return anotationInfo.getType().getClassName().equals(Controller.class.getName());
    }

    private boolean isRequestBody(ClassBean.AnotationInfo anotation) {
        return anotation.getType().getClassName().equals(RequestBody.class.getName());
    }
}
