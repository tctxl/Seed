package com.opdar.framework.web;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.SeedWeakClassloader;
import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.asm.Type;
import com.opdar.framework.db.impl.BaseDatabase;
import com.opdar.framework.db.impl.DaoMap;
import com.opdar.framework.db.impl.OnDataSourceCloseListener;
import com.opdar.framework.utils.ParamsUtil;
import com.opdar.framework.utils.ThreadLocalUtils;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.anotations.*;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.common.*;
import com.opdar.framework.web.exceptions.ParamUnSupportException;
import com.opdar.framework.web.interfaces.HttpConvert;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.parser.FormParser;
import com.opdar.framework.web.parser.HttpParser;
import com.opdar.framework.web.views.DefaultView;
import com.opdar.framework.web.views.ErrorView;
import com.opdar.framework.web.views.FileView;
import com.opdar.framework.web.views.HtmlView;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedWeb {
    private final Log log = LogFactory.getLog("SeedWeb");
    //所有路由
    private static final HashMap<String, SeedRouter> routers = new HashMap<String, SeedRouter>();
    private static final HashMap<String, Integer> controllerSort = new HashMap<String, Integer>();
    private static final HashMap<Integer, Class<?>> controllerAfters = new HashMap<Integer, Class<?>>();
    private static final HashMap<Integer, Class<?>> controllerBefores = new HashMap<Integer, Class<?>>();
    private static final HashMap<String, Class<?>> routerAfters = new HashMap<String, Class<?>>();
    private static final HashMap<String, Class<?>> routerBefores = new HashMap<String, Class<?>>();
    private static final HashMap<String, ThreadLocal<Object>> threadMaps = new HashMap<String, ThreadLocal<Object>>();
    private static final HashMap<String, HashMap<String, ClassBean>> argsMapped = new HashMap<String, HashMap<String, ClassBean>>();
    private static final HashMap<String, HttpConvert> converts = new HashMap<String, HttpConvert>();
    private static final Map<String, HttpParser> parsers = new HashMap<String, HttpParser>();
    private static final HashSet<String> defaultPages = new HashSet<String>();
    public static String WEB_HTML_PATH = "";
    public static final HashMap<String, SeedPath> publicPaths = new HashMap<String, SeedPath>();
    public static Map<String, String> RESOURCE_MAPPING = new HashMap<String, String>();
    private static final String HTTP_THREAD_KEY = "HTTP_THREAD_KEY";
    private static final List<String> restfulList = new LinkedList<String>();

    static {
        defaultPages.add("INDEX.HTML");
        defaultPages.add("DEFAULT.HTML");
        String seedRoot = System.getProperty("seed.root");
        if (seedRoot == null) {
            System.setProperty("seed.root", seedRoot = SeedWeb.class.getResource("/").getPath());
        }
    }

    private static ThreadLocal<SeedResponse> sharedResponse = new ThreadLocal<SeedResponse>();
    private static ThreadLocal<SeedRequest> sharedRequest = new ThreadLocal<SeedRequest>();

    public SeedWeb() {
        log.debug("seed.root path is ".concat(System.getProperty("seed.root")));
        setParser(new FormParser());
    }

    public void destory() {
        for (Iterator<Map.Entry<String, ThreadLocal<Object>>> it = threadMaps.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ThreadLocal<Object>> entry = it.next();
            ThreadLocal<Object> local = entry.getValue();
            ThreadLocalUtils.clearThreadLocals(HTTP_THREAD_KEY, local);
        }
        DaoMap.clear();
        BaseDatabase database = Context.get(BaseDatabase.class);
        if (database != null) {
            database.close();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 页面名称分号分割
     *
     * @param pages
     */
    public void setDefaultPages(String pages) {
        if (pages != null && pages.trim().length() > 0) {
            int index = pages.indexOf(";");
            if (index == -1) {
                defaultPages.add(pages.toUpperCase());

            } else {
                String[] ps = pages.split(";");
                for (int i = 0; i < ps.length; i++) {
                    String p = ps[i].trim();
                    if (p.length() > 0)
                        defaultPages.add(p.toUpperCase());
                }
            }
            log.debug("Default Page : ".concat(defaultPages.toString()));
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

    public void setParser(HttpParser parser) {
        parsers.put(parser.getContentType(), parser);
    }

    public HttpParser getParser(String contentType) {
        if (parsers.containsKey(contentType))
            return parsers.get(contentType);
        return null;
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
            if(c.isAnonymousClass())continue;
            SeedInvoke.init(c);
            ClassBean classBean = SeedInvoke.getBeanSymbols().get(c);
            Type controllerAfter = null;
            Type controllerBefore = null;
            int sort = controllerSort.size();
            for (ClassBean.AnotationInfo anotationInfo : classBean.getAnotations()) {
                boolean isController = anotationInfo.getType().getClassName().equals(Controller.class.getName());
                boolean isAfter = anotationInfo.getType().getClassName().equals(After.class.getName());
                boolean isBefore = anotationInfo.getType().getClassName().equals(Before.class.getName());
                if (isAfter) {
                    controllerAfter = (Type) anotationInfo.getValue().get(0).getValue();
                    continue;
                }
                if (isBefore) {
                    controllerBefore = (Type) anotationInfo.getValue().get(0).getValue();
                    continue;
                }
                if (isController) {
                    controllerSort.put(classBean.getSeedClz().getName(), sort);
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
                        String routerName = null;
                        Type after = null, before = null;
                        for (ClassBean.AnotationInfo methodAnotation : methodInfo.getAnotations()) {
                            boolean isRouter = methodAnotation.getType().getClassName().equals(Router.class.getName());
                            boolean isRouterAfter = methodAnotation.getType().getClassName().equals(After.class.getName());
                            boolean isRouterBefore = methodAnotation.getType().getClassName().equals(Before.class.getName());
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
                            List<String> restfulPar = new ArrayList<String>();

                            routerName = Utils.parseSignFactor(routerName,restfulPar).replace(" %s ",".*");
                            String router = Utils.testRouter(controllerRouter).concat(Utils.testRouter(routerName));

                            String p1 = prefixName;
                            SeedRouter seedRouter = new SeedRouter();
                            seedRouter.setRouterName(routerName.concat(p1));
                            if(restfulPar.size()>0){
                                restfulList.add(router.concat(p1.replace(".", "\\.")).toUpperCase());
                                p1 = p1.replace(".","\\.");
                                seedRouter.setRestfulPar(restfulPar);
                            }
                            routers.put(router.toUpperCase().concat(p1), seedRouter);
                            seedRouter.setClassBean(classBean);
                            seedRouter.setMethodInfo(methodInfo);
                        }
                        if (after != null) {
                            try {
                                Class clz = Thread.currentThread().getContextClassLoader().loadClass(after.getClassName());
                                routerAfters.put(routerName, clz);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        if (before != null) {
                            try {
                                Class clz = Thread.currentThread().getContextClassLoader().loadClass(before.getClassName());
                                routerBefores.put(routerName, clz);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    continue;
                }
            }
            if (controllerAfter != null) {
                try {
                    Class clz = Thread.currentThread().getContextClassLoader().loadClass(controllerAfter.getClassName());
                    controllerAfters.put(sort, clz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (controllerBefore != null) {
                try {
                    Class clz = Thread.currentThread().getContextClassLoader().loadClass(controllerBefore.getClassName());
                    controllerBefores.put(sort, clz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        createRouterArgsMapped();
    }

    private static final Map<String, String> contentTypes = new HashMap<String, String>();

    public static ThreadLocal<SeedResponse> SharedResponse() {
        return sharedResponse;
    }

    public static ThreadLocal<SeedRequest> SharedRequest() {
        return sharedRequest;
    }

    public void execute(String routerName, SeedRequest request, final IResponse response) {
        ThreadLocalUtils.record(HTTP_THREAD_KEY);
        final SeedResponse seedResponse = (SeedResponse) response;
        try {
            sharedResponse.set(seedResponse);
            sharedRequest.set(request);
            routerName = routerName.toUpperCase();
            SeedPath publicPath = null;
            int pNameIndex = -1;
            if ((pNameIndex = routerName.indexOf("/", 1)) != -1) {
                String publicPathKey = routerName.substring(0, pNameIndex);
                if (SeedWeb.publicPaths.containsKey(publicPathKey))
                    publicPath = SeedWeb.publicPaths.get(publicPathKey);
            }
            FileView.FileReadListener fileReadListener = new FileView.FileReadListener() {
                @Override
                public void read(byte[] bytes, String contentType, int responseCode) {
                    seedResponse.write(bytes, contentType, responseCode);
                }

                @Override
                public void catchException(Throwable throwable) {
                    ErrorView error = new ErrorView(HttpResponseCode.CODE_404);
                    seedResponse.write(error.renderView(), error.contentType(), error.getCode());
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
                if (contentTypes.containsKey(res)) {
                    contentType = contentTypes.get(res);
                } else {
                    contentType = new MimetypesFileTypeMap().getContentType(res.toLowerCase());
                    contentTypes.put(res, contentType);
                }
                try {
                    if (view == null) {
                        if (publicPath.getPathType() == 1) {
                            view = new FileView(publicPath.getResourceAsStream(res), contentType, fileReadListener);
                        } else {
                            view = new FileView(publicPath.getFilePath(res), contentType, fileReadListener);
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
                    if (view.headers() != null)
                        response.setHeaders(view.headers());
                    if (view instanceof HtmlView) {
                        ((HtmlView) view).setFileReadListener(fileReadListener).renderView();
                    } else {
                        byte[] result = view.renderView();
                        seedResponse.write(result, view.contentType(), view.getCode());
                        seedResponse.flush();
                    }
                }
            }
        } finally {
            sharedRequest.remove();
            sharedResponse.remove();
            sharedRequest.set(null);
            sharedResponse.set(null);
        }
    }

    public View executeLogic(String routerName, SeedRequest request) {
        HashMap<String, SeedExcuteItrf> aopObjs = new HashMap<String, SeedExcuteItrf>();
        SeedRouter router = null;
        if (routers.containsKey(routerName.toUpperCase())) {
            router = routers.get(routerName.toUpperCase());
        }
        if (router == null) {
            for (String p : defaultPages) {
                String testP = Utils.testRouter(routerName.toUpperCase()) + Utils.testRouter(p);
                if (routers.containsKey(testP)) {
                    router = routers.get(testP);
                    routerName = testP;
                    break;
                }
            }
        }
        HashMap<String,String> restfulResult = new HashMap<String, String>();
        if(router == null){
            for (String restfulPath : restfulList){

                if(routerName.matches(restfulPath) && routers.containsKey(restfulPath)){
                    router = routers.get(restfulPath);
                    String[] sp = restfulPath.replaceAll("\\.\\*","&").split("&");
                    String tempRouterName = routerName;
                    for(String s:sp){
                        tempRouterName = tempRouterName.replaceAll(s,"&");
                    }
                    int tempIndex = -1;
                    if((tempIndex = tempRouterName.indexOf("&"))>=0){
                        String[] result = tempRouterName.split("&");
                        int plus = tempIndex == 0?1:0;
                        for(int i=0;i<router.getRestfulPar().size();i++){
                            String key = router.getRestfulPar().get(i);
                            String value = null;
                            if(i < result.length ){
                                value = result[i+plus];
                            }
                            restfulResult.put(key,value);
                        }
                    }
                    routerName = restfulPath;
                }
            }
        }
        if (router != null) {
            int index = controllerSort.get(router.getClassBean().getSeedClz().getName());
            SeedExcuteItrf a1 = null, a2 = null, b1 = null, b2 = null;
            try {
                if (controllerAfters.containsKey(index)) {
                    try {
                        Class cls = controllerAfters.get(index);
                        if (aopObjs.containsKey(cls.getName())) {
                            a1 = aopObjs.get(cls.getName());
                        } else {
                            a1 = SeedInvoke.buildObject(cls);
                            aopObjs.put(cls.getName(), a1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (controllerBefores.containsKey(index)) {
                    try {
                        Class cls = controllerBefores.get(index);
                        if (aopObjs.containsKey(cls.getName())) {
                            b1 = aopObjs.get(cls.getName());
                        } else {
                            b1 = SeedInvoke.buildObject(cls);
                            aopObjs.put(cls.getName(), b1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (routerAfters.containsKey(router.getRouterName())) {
                    try {
                        Class cls = routerAfters.get(router.getRouterName());
                        if (aopObjs.containsKey(cls.getName())) {
                            a2 = aopObjs.get(cls.getName());
                        } else {
                            a2 = SeedInvoke.buildObject(cls);
                            aopObjs.put(cls.getName(), a2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (routerBefores.containsKey(router.getRouterName())) {
                    try {
                        Class cls = routerBefores.get(router.getRouterName());
                        if (aopObjs.containsKey(cls.getName())) {
                            b2 = aopObjs.get(cls.getName());
                        } else {
                            b2 = SeedInvoke.buildObject(cls);
                            aopObjs.put(cls.getName(), b2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Object ret = null;
                try {
                    if (b2 != null) {
                        ret = b2.invokeMethod("before");
                    }
                } catch (Exception e) {
                }finally {
                    if(ret != null){
                        if(ret instanceof Boolean){
                            if(!(Boolean)ret){
                                return new ErrorView(HttpResponseCode.CODE_401);
                            }
                        }else
                        if (ret instanceof View) {
                            return (View) ret;
                        }else{
                            return new DefaultView(ret);
                        }
                    }
                }

                try {
                    ret = null;

                    if (b1 != null) {
                        ret = b1.invokeMethod("before");
                    }
                } catch (Exception e) {
                }finally {
                    if(ret != null){
                        if(ret instanceof Boolean){
                            if(!(Boolean)ret){
                                return new ErrorView(HttpResponseCode.CODE_401);
                            }
                        }else
                        if (ret instanceof View) {
                            return (View) ret;
                        }else{
                            return new DefaultView(ret);
                        }
                    }
                }

                ThreadLocal<Object> threadLocal = null;
                String threadKey = router.getClassBean().getSeedClz().getSuperclass().getName();
                if (threadMaps.containsKey(threadKey)) {
                    threadLocal = threadMaps.get(threadKey);
                } else {
                    threadLocal = new ThreadLocal<Object>();
                    threadMaps.put(threadKey, threadLocal);
                }
                if (threadLocal.get() == null) {
                    try {
                        threadLocal.set(router.getClassBean().getSeedClz().newInstance());
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
                            params = execLogicRequestBody(routerName, router, request,restfulResult);
                        } catch (ParamUnSupportException e) {
                            return new ErrorView(HttpResponseCode.CODE_415);
                        }
                    } else {
                        params = execLogicNormal(routerName, request.getValues(), router,restfulResult);
                    }

                    boolean isVoid = router.getMethodInfo().getType().getReturnType().getClassName().equals("void");
                    Object seedExcuteItrf = threadLocal.get();
                    Object result = ((SeedExcuteItrf)seedExcuteItrf).invokeMethod(router.getMethodInfo().getName(), params);
                    if (isVoid) {
                        return null;
                    }
                    if (result instanceof View) {
                        return (View) result;
                    }
                    return new DefaultView(result);
                }
            } finally {
                try {
                    if (a2 != null) {
                        a2.invokeMethod("after");
                    }
                } catch (Exception e) {
                }

                try {
                    if (a1 != null) {
                        a1.invokeMethod("after");
                    }
                } catch (Exception e) {
                }
            }
        }
        return new ErrorView(HttpResponseCode.CODE_404);
    }

    private Object[] execLogicRequestBody(String routerName, SeedRouter router, SeedRequest request, HashMap<String, String> restfulResult) throws ParamUnSupportException {
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
            }else
            if(restfulResult.containsKey(localVar.getName())){
                value = restfulResult.get(localVar.getName());
            }
            params[i] = value;
        }
        return params;
    }

    private Object[] execLogicNormal(String routerName, Map<String, Object> values, SeedRouter router, HashMap<String, String> restfulResult) {
        Object[] params = new Object[router.getMethodInfo().getArgs().length];
        HashMap<String, ClassBean> mapped = argsMapped.get(routerName.toUpperCase());
        HashMap<String, Integer> sorts = router.getMethodInfo().getArgsSort();
        for (Iterator<String> it = values.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            ClassBean classBean = mapped.get(key);
            if(!router.getMethodInfo().getArgsSort().containsKey(key))continue;
            Integer index = router.getMethodInfo().getArgsSort().get(key);
            Type type = router.getMethodInfo().getArgs()[index];
//            if(restfulResult.containsKey(key))
            if (classBean == null && (type.getSort() != 10 || type.getClassName().equals(String.class.getName()))) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(restfulResult.size()>0){
            for(Iterator<String> it = restfulResult.keySet().iterator(); it.hasNext();){
                String key = it.next();
                params[sorts.get(key)] = (restfulResult.get(key));
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
        RESOURCE_MAPPING.putAll(ParamsUtil.getResourceMapping(webHtml));
    }

    public void setWebPublic(String webPublic) {
        Map<String, String> values = Utils.spliteParams(webPublic);
        for (Iterator<String> it = values.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = values.get(key);
            SeedPath path = new SeedPath(key, value);
            publicPaths.put(key.toUpperCase(), path);

            RESOURCE_MAPPING.putAll(ParamsUtil.getResourceMapping(path.getPath()));
        }
    }

    public void setDatabase(String activeRecord, String driver, String jdbcUrl, String userName, String passWord, String databaseName, String datasourceClass, String host, String openurl) {
        try {
            if (Integer.parseInt(activeRecord) == 1) {
                HikariConfig config = new HikariConfig();
                if(jdbcUrl != null){
                    config.setJdbcUrl(jdbcUrl);
                }
                if(driver !=null){
                    config.setDriverClassName(driver);
                }

                if (Integer.parseInt(openurl) == 0) {
                    config.setMaximumPoolSize(100);
                    config.setDataSourceClassName(datasourceClass);
                    config.addDataSourceProperty("serverName", host);
                    config.addDataSourceProperty("port", "3306");
                    config.addDataSourceProperty("databaseName", databaseName);
                    config.addDataSourceProperty("user", userName);
                    config.addDataSourceProperty("password", passWord);
                }else{
                    config.setUsername(userName);
                    config.setPassword(passWord);
                }

                config.setConnectionTestQuery("select 1");
                config.setConnectionTimeout(3000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                config.setMaximumPoolSize(50);
                config.setMinimumIdle(100);
                HikariDataSource hikariDataSource = new HikariDataSource(config);
                BaseDatabase database = new BaseDatabase(hikariDataSource, new OnDataSourceCloseListener() {

                    @Override
                    public void close(DataSource dataSource) {
                        if (dataSource != null && dataSource instanceof HikariDataSource) {
                            try {
                                ((HikariDataSource) dataSource).close();
                                DriverManager.deregisterDriver(DriverManager.getDriver(((HikariDataSource) dataSource).getJdbcUrl()));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Context.add(database);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
