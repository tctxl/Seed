package com.opdar.framework.web;

import com.opdar.framework.aop.SeedAop;
import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.SeedWeakClassloader;
import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.asm.Type;
import com.opdar.framework.utils.ParamsUtil;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.RequestBody;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.common.*;
import com.opdar.framework.web.exceptions.ParamUnSupportException;
import com.opdar.framework.web.interfaces.HttpConvert;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.views.DefaultView;
import com.opdar.framework.web.views.ErrorView;
import com.opdar.framework.web.views.FileView;
import com.opdar.framework.web.views.HtmlView;
import sun.awt.image.URLImageSource;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedWeb {

    //所有路由
    private static final HashMap<String, SeedRouter> routers = new HashMap<String, SeedRouter>();
    private static final HashMap<String, Integer> controllerSort = new HashMap<String, Integer>();
    private static final HashMap<Integer, ThreadLocal<SeedExcuteItrf>> controllerObjects = new HashMap<Integer, ThreadLocal<SeedExcuteItrf>>();
    private static final HashMap<String, HashMap<String, ClassBean>> argsMapped = new HashMap<String, HashMap<String, ClassBean>>();
    private static final HashMap<String, HttpConvert> converts = new HashMap<String, HttpConvert>();
    private static final HashSet<String> defaultPages = new HashSet<String>();
    public static String WEB_HTML_PATH = "";
    public static final HashMap<String,SeedPath> publicPaths = new HashMap<String, SeedPath>();

    static{
        defaultPages.add("INDEX.HTML");
        defaultPages.add("DEFAULT.HTML");
        System.setProperty("seed.root",SeedWeb.class.getResource("/").getPath());
    }

    public SeedWeb(){
    }

    private static final SeedAop defaultAop = new SeedAop() {
        public void before() {

        }

        public void after() {

        }
    };

    /**
     * 页面名称分号分割
     * @param pages
     */
    public void setDefaultPages(String pages){
        if(pages !=null && pages.trim().length()>0){
            int index = pages.indexOf(";");
            if(index == -1){
                defaultPages.add(pages.toUpperCase());
            }else{
                String[] ps = pages.split(";");
                for(int i=0;i<ps.length;i++){
                    String p = ps[i].trim();
                    if(p.length() > 0)
                        defaultPages.add(p.toUpperCase());
                }
            }
        }
    }

    /**
     * 创建路由参数映射表
     */
    public void createRouterArgsMapped() {
        for (Iterator<String> it = routers.keySet().iterator(); it.hasNext(); ) {
            String routerName = it.next();
            SeedRouter router = routers.get(routerName);
            HashMap<String, ClassBean> table = null;
            if (argsMapped.containsKey(routerName)) {
                table = argsMapped.get(routerName);
            } else {
                argsMapped.put(routerName, table = new HashMap<String, ClassBean>());
            }
            for (ClassBean.MethodInfo.LocalVar var : router.getMethodInfo().getLocalVars()) {
                for (ClassBean.AnotationInfo anotation : var.getAnnotations()) {
                    boolean hasRequestBody = anotation.getType().getClassName().equals(RequestBody.class.getName());
                    if (hasRequestBody) {
                        router.setRequestBody(true, var);
                        break;
                    }
                }
                switch (var.getType().getSort()) {
                    case Type.OBJECT:
                        if (!var.getType().getClassName().equals(String.class.getName())) {
                            if (var.getType().getClassName().matches("java.util.*?List")) {
                                if (var.getSignatureTypes().size() > 1) {
                                    String className = var.getSignatureTypes().get(var.getSignatureTypes().size() - 2).replace("/", ".");
                                    try {
                                        Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                                        SeedInvoke.init(clz);
                                        ClassBean classBean = SeedInvoke.getBeanSymbols().get(clz);
                                        for (ClassBean.FieldInfo fieldInfo : classBean.getField()) {
                                            table.put(fieldInfo.getName(), classBean);
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    Class clz = Thread.currentThread().getContextClassLoader().loadClass(var.getType().getClassName());
                                    SeedInvoke.init(clz);
                                    ClassBean classBean = SeedInvoke.getBeanSymbols().get(clz);
                                    for (ClassBean.FieldInfo fieldInfo : classBean.getField()) {
                                        table.put(fieldInfo.getName(), classBean);
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            table.put(var.getName(), null);
                        }
                        break;
                    default:
                        table.put(var.getName(), null);
                        break;
                }
            }

        }

    }


    public void scanConverts(String packageName) {
        converts.clear();
        Set<Class<?>> convertsClz = ParamsUtil.getClasses(packageName);
        for (Class<?> c : convertsClz) {
            try {
                HttpConvert convert = (HttpConvert) c.newInstance();
                converts.put(convert.getContentType(), convert);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHttpConvert(Class<? extends HttpConvert> convertClz) {
        HttpConvert convert = null;
        try {
            convert = convertClz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        converts.put(convert.getContentType(), convert);
    }

    public void setHttpConvert(HttpConvert convert) {
        converts.put(convert.getContentType(), convert);
    }


    public void scanController(String packageName) {
        scanController(packageName, true);
    }

    /**
     * 扫描控制器并生成路由
     *
     * @param packageName 包名
     */
    public void scanController(String packageName, boolean isClear) {
        if (isClear) routers.clear();
        Set<Class<?>> controllersClz = ParamsUtil.getClasses(packageName);
        for (Class<?> c : controllersClz) {
            SeedInvoke.init(c);
            ClassBean classBean = SeedInvoke.getBeanSymbols().get(c);
            for (ClassBean.AnotationInfo anotationInfo : classBean.getAnotations()) {
                boolean isController = anotationInfo.getType().getClassName().equals(Controller.class.getName());
                if (isController) {
                    controllerSort.put(classBean.getSeedClz().getName(), controllerSort.size());
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
                    for (ClassBean.MethodInfo methodInfo : methods) {
                        for (ClassBean.AnotationInfo methodAnotation : methodInfo.getAnotations()) {
                            boolean isRouter = methodAnotation.getType().getClassName().equals(Router.class.getName());
                            if (isRouter) {
                                String routerName = methodInfo.getName();
                                if (methodAnotation.getValue().size() > 0) {
                                    ClassBean.AnotationInfo.AnotationValue routerValue = methodAnotation.getValue().get(0);
                                    if (!routerValue.getValue().equals("")) {
                                        routerName = routerValue.getValue().toString();
                                    }
                                }

                                String router = Utils.testRouter(controllerRouter).concat(Utils.testRouter(routerName));
                                SeedRouter seedRouter = new SeedRouter();
                                seedRouter.setClassBean(classBean);
                                seedRouter.setMethodInfo(methodInfo);
                                seedRouter.setRouterName(routerName.concat(prefixName));
                                routers.put(router.toUpperCase().concat(prefixName), seedRouter);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        createRouterArgsMapped();
    }

    private static final Map<String,String> contentTypes = new HashMap<String, String>();

    public void execute(String routerName, SeedRequest request, final IResponse response) {
        defaultAop.before();
        final SeedResponse seedResponse = (SeedResponse) response;
        routerName = routerName.toUpperCase();
        SeedPath publicPath = null;
        int pNameIndex = -1;
        if((pNameIndex = routerName.indexOf("/",1)) != -1){
            String publicPathKey = routerName.substring(0,pNameIndex);
            if(SeedWeb.publicPaths.containsKey(publicPathKey))
                publicPath = SeedWeb.publicPaths.get(publicPathKey);
        }
        FileView.FileReadListener fileReadListener = new FileView.FileReadListener() {
            @Override
            public void read(byte[] bytes,String contentType,int responseCode) {
                seedResponse.write(bytes,contentType,responseCode);
            }

            @Override
            public void catchException(Throwable throwable) {
                ErrorView error = new ErrorView(HttpResponseCode.CODE_404);
                seedResponse.write(error.renderView(),error.contentType(),error.getCode());
            }

            @Override
            public void close() {
                seedResponse.flush();
            }
        };

        View view = null;
        if (publicPath != null) {
            String res = Utils.testRouter(routerName.replace(publicPath.getMapping(), ""));
            String contentType = "text/html";
            if(contentTypes.containsKey(res)){
                contentType = contentTypes.get(res);
            }else{
                contentType =  new MimetypesFileTypeMap().getContentType(res);
                contentTypes.put(res,contentType);
            }
            try {
                if (view == null){
                    if(publicPath.getPathType() == 1){
                        view = new FileView(publicPath.getResourceAsStream(res), contentType, fileReadListener);
                    }else{
                        view = new FileView(publicPath.getFilePath(res),contentType,fileReadListener);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = view.renderView();
            if (data == null && !(view instanceof FileView)) {
                view = new ErrorView(HttpResponseCode.CODE_404);
                data = view.renderView();
                seedResponse.write(data, view.contentType(), view.getCode());
                seedResponse.flush();
            }
        } else {
            view = executeLogic(routerName, request);
            if (view == null) {
                //return void;
                seedResponse.writeSuccess();
            } else {
                if(view instanceof HtmlView){
                    ((HtmlView) view).setFileReadListener(fileReadListener).renderView();
                }else{
                    byte[] result = view.renderView();
                    seedResponse.write(result, view.contentType(), view.getCode());
                    seedResponse.flush();
                }
            }
        }
        defaultAop.after();
    }

    public View executeLogic(String routerName, SeedRequest request) {
        SeedRouter router = null;
        if (routers.containsKey(routerName.toUpperCase())) {
            router = routers.get(routerName.toUpperCase());
        }
        if(router == null){
            for(String p:defaultPages){
                String testP = Utils.testRouter(routerName.toUpperCase())+Utils.testRouter(p);
                if(routers.containsKey(testP)){
                    router = routers.get(testP);
                    routerName = testP;
                    break;
                }
            }
        }
        if(router !=null){
            int index = controllerSort.get(router.getClassBean().getSeedClz().getName());
            ThreadLocal<SeedExcuteItrf> threadLocal = null;
            if (controllerObjects.containsKey(index)) {
                threadLocal = controllerObjects.get(index);
            } else {
                threadLocal = new ThreadLocal<SeedExcuteItrf>();
                controllerObjects.put(index, threadLocal);
            }
            if (threadLocal.get() == null) {
                try {
                    threadLocal.set((SeedExcuteItrf) router.getClassBean().getSeedClz().newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (argsMapped.containsKey(routerName.toUpperCase())) {
                Object[] params = null;
                if (router.hasRequestBody()) {
                    try {
                        params = execLogicRequestBody(routerName, router, request);
                    } catch (ParamUnSupportException e) {
                        return new ErrorView(HttpResponseCode.CODE_415);
                    }
                } else {
                    params = execLogicNormal(routerName, request.getValues(), router);
                }

                boolean isVoid = router.getMethodInfo().getType().getReturnType().getClassName().equals("void");
                Object result = threadLocal.get().invokeMethod(router.getMethodInfo().getName(), params);
                if (isVoid) {
                    return null;
                }
                if (result instanceof View) {
                    return (View) result;
                }
                return new DefaultView(result);
            }
        }
        return new ErrorView(HttpResponseCode.CODE_404);
    }

    private Object[] execLogicRequestBody(String routerName, SeedRouter router, SeedRequest request) throws ParamUnSupportException {
        Object[] params = new Object[router.getMethodInfo().getArgs().length];
        for (int i = 0; i < router.getMethodInfo().getLocalVars().size(); i++) {
            ClassBean.MethodInfo.LocalVar localVar = router.getMethodInfo().getLocalVars().get(i);
            Object value = null;
            if (localVar.equals(router.getRequestBodyVar())) {
                String contentType = request.getContentType();
                if (converts.containsKey(contentType)) {
                    HttpConvert convert = converts.get(contentType);
                    String sig = localVar.getSignature();
                    if (sig == null) sig = localVar.getDesc();
                    java.lang.reflect.Type type = SeedWeakClassloader.getType(sig);
                    value = convert.readBody(request.getBody(), type);
                } else {
                    throw new ParamUnSupportException(String.format("contentType[%s]不被RequestBody支持", contentType));
                }
            }
            params[i] = value;
        }
        return params;
    }

    private Object[] execLogicNormal(String routerName, Map<String, String> values, SeedRouter router) {
        Object[] params = new Object[router.getMethodInfo().getArgs().length];
        HashMap<String, ClassBean> mapped = argsMapped.get(routerName.toUpperCase());
        for (Iterator<String> it = values.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            HashMap<String, Integer> sorts = new HashMap<String, Integer>();
            sorts.putAll(router.getMethodInfo().getArgsSort());
            ClassBean classBean = mapped.get(key);
            if (classBean == null) {
                if (sorts.containsKey(key))
                    params[sorts.get(key)] = (values.get(key));
                continue;
            }
            try {
                SeedExcuteItrf execute = (SeedExcuteItrf) classBean.getSeedClz().newInstance();
                execute.invokeMethod(productSetMethodName(key), values.get(key));
                for (ClassBean.MethodInfo.LocalVar localVar : router.getMethodInfo().getLocalVars()) {
                    if (localVar.getType().getClassName().equals(classBean.getSeedClz().getSuperclass().getName())) {
                        params[sorts.get(localVar.getName())] = (execute);
                        break;
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    private String productSetMethodName(String fieldName) {
        if (fieldName.length() < 3) return "set".concat(fieldName);
        char c1 = Character.toUpperCase(fieldName.charAt(0));
        boolean c2IsUpper = Character.isUpperCase(fieldName.charAt(1));
        if (c2IsUpper) return "set".concat(fieldName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("set");
        stringBuilder.append(c1);
        stringBuilder.append(fieldName.substring(1, fieldName.length()));
        return stringBuilder.toString();
    }

    public void setWebHtml(String webHtml) {
        SeedWeb.WEB_HTML_PATH = webHtml.replace(".", "/");
    }

    public void setWebPublic(String webPublic) {
        Map<String, String> values = Utils.spliteParams(webPublic);
        for(Iterator<String> it = values.keySet().iterator();it.hasNext();){
            String key = it.next();
            String value = values.get(key);
            SeedPath path = new SeedPath(key,value);
            publicPaths.put(key.toUpperCase(),path);
        }
    }

}
