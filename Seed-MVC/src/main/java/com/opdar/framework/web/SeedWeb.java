package com.opdar.framework.web;

import com.opdar.framework.aop.SeedWeakClassloader;
import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.db.impl.BaseDatabase;
import com.opdar.framework.db.impl.DaoMap;
import com.opdar.framework.db.impl.OnDataSourceCloseListener;
import com.opdar.framework.utils.ParamsUtil;
import com.opdar.framework.utils.ThreadLocalUtils;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.anotations.Component;
import com.opdar.framework.web.anotations.Inject;
import com.opdar.framework.web.common.*;
import com.opdar.framework.web.exceptions.ParamUnSupportException;
import com.opdar.framework.web.interfaces.HttpConvert;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.parser.FormParser;
import com.opdar.framework.web.parser.HttpParser;
import com.opdar.framework.web.utils.ArgInvoke;
import com.opdar.framework.web.utils.ControllerInvoke;
import com.opdar.framework.web.utils.RestfulInvoke;
import com.opdar.framework.web.views.DefaultView;
import com.opdar.framework.web.views.ErrorView;
import com.opdar.framework.web.views.FileView;
import com.opdar.framework.web.views.HtmlView;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public static final HashMap<String, SeedPath> publicPaths = new HashMap<String, SeedPath>();
    //所有路由
    private static final HashMap<String, ThreadLocal<Object>> threadMaps = new HashMap<String, ThreadLocal<Object>>();
    //转换器
    private static final HashMap<String, HttpConvert> converts = new HashMap<String, HttpConvert>();
    //输入流解析器
    private static final Map<String, HttpParser> parsers = new HashMap<String, HttpParser>();
    //默认页
    private static final HashSet<String> defaultPages = new HashSet<String>();
    private static final String HTTP_THREAD_KEY = "HTTP_THREAD_KEY";
    private static final Map<String, String> contentTypes = new HashMap<String, String>();
    public static String WEB_HTML_PATH = "";
    public static Map<String, String> RESOURCE_MAPPING = new HashMap<String, String>();
    private static ThreadLocal<SeedResponse> sharedResponse = new ThreadLocal<SeedResponse>();
    private static ThreadLocal<SeedRequest> sharedRequest = new ThreadLocal<SeedRequest>();

    static {
        defaultPages.add("INDEX.HTML");
        defaultPages.add("DEFAULT.HTML");
        String seedRoot = System.getProperty("seed.root");
        if (seedRoot == null) {
            System.setProperty("seed.root", seedRoot = SeedWeb.class.getResource("/").getPath());
        }
    }

    private final Logger log = LoggerFactory.getLogger("SeedWeb");
    private ControllerInvoke controllerInvoke = new ControllerInvoke();
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public SeedWeb() {
        log.debug("seed.root path is ".concat(System.getProperty("seed.root")));
        setParser(new FormParser());
    }

    public static ThreadLocal<SeedResponse> SharedResponse() {
        return sharedResponse;
    }

    public static ThreadLocal<SeedRequest> SharedRequest() {
        return sharedRequest;
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

    public void scanConverts(String packageName) {
        converts.clear();
        Set<Class<?>> convertsClz = ParamsUtil.getClasses(loader, packageName);
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
        if (contentType != null) {
            String[] cs = null;
            if (contentType.indexOf(";") != -1) {
                cs = contentType.split(";");
            } else {
                cs = new String[]{contentType};
            }
            for (String s : cs) {
                if (parsers.containsKey(s))
                    return parsers.get(s);
            }
        }
        return null;
    }

    public void loadComponent(String packageName) {
        loadComponent(packageName, true, null);
    }

    /**
     * 扫描控制器并生成路由
     *
     * @param packageName 包名
     */
    public void loadComponent(String packageName, boolean isClear, String perfix) {
        synchronized (this) {
            if (isClear) controllerInvoke = new ControllerInvoke();
            controllerInvoke.scan(packageName, perfix, loader);
            invokeComponent();
        }
    }

    private void invokeComponent() {
        Set<Class<?>> components = ParamsUtil.getClasses(loader, "");
        for (Class<?> clz : components) {
            Component component = clz.getAnnotation(Component.class);
            if (component != null) {
                Class context = null;
                try {
                    context = loader.loadClass(Context.class.getName());
                    Method method = context.getMethod("addComponent", Class.class);
                    method.invoke(null, clz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void execute(String routerName, SeedRequest request, final IResponse response) {
        ThreadLocalUtils.record(HTTP_THREAD_KEY);
        final SeedResponse seedResponse = (SeedResponse) response;
        try {
            sharedResponse.set(seedResponse);
            sharedRequest.set(request);
            routerName = routerName.toUpperCase();
            SeedPath publicPath = getSeedPath(routerName);

            FileView.FileReadListener fileReadListener = getFileReadListener(seedResponse);

            View view = null;
            if (publicPath != null) {
                executePublicPath(routerName, seedResponse, publicPath, fileReadListener, view);
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

    private FileView.FileReadListener getFileReadListener(final SeedResponse seedResponse) {
        return new FileView.FileReadListener() {
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
    }

    private SeedPath getSeedPath(String routerName) {
        SeedPath publicPath = null;
        int pNameIndex = -1;
        if ((pNameIndex = routerName.indexOf("/", 1)) != -1) {
            String publicPathKey = routerName.substring(0, pNameIndex);
            if (SeedWeb.publicPaths.containsKey(publicPathKey))
                publicPath = SeedWeb.publicPaths.get(publicPathKey);
        }
        return publicPath;
    }

    private void executePublicPath(String routerName, SeedResponse seedResponse, SeedPath publicPath, FileView.FileReadListener fileReadListener, View view) {
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
    }

    public View executeLogic(String routerName, SeedRequest request) {
        SeedRouter router = null;

        if (controllerInvoke.containsRouter(routerName)) {
            router = controllerInvoke.invokeDefaultRouter(routerName);
        }

        if (router == null) {
            for (String p : defaultPages) {
                String testP = Utils.testRouter(routerName.toUpperCase()) + Utils.testRouter(p);
                if (controllerInvoke.containsRouter(testP)) {
                    router = controllerInvoke.invokeDefaultRouter(testP);
                    routerName = testP;
                    break;
                }
            }
        }

        HashMap<String, String> restfulResult = new HashMap<String, String>();
        if (router == null) {
            RestfulInvoke restfulInvoke = controllerInvoke.invokeRestfulRouter(routerName);
            if (restfulInvoke != null) {
                routerName = restfulInvoke.getRouterName();
                router = restfulInvoke.getRouter();
                restfulResult = restfulInvoke.getRestfulResult();
            }
        }
        if (router != null) {
            try {
                Object ret = router.getControllerAct().invokeBefore();
                if (ret != null && (!(ret instanceof Boolean) || !(Boolean) ret)) {
                    return (View) ret;
                }
                ret = router.invokeBefore();
                if (ret != null && (!(ret instanceof Boolean) || !(Boolean) ret)) {
                    return (View) ret;
                }
                ThreadLocal<Object> threadLocal = getController(router);

                Object[] params = null;
                if (router.hasRequestBody()) {
                    try {
                        params = execLogicRequestBody(routerName, router, request, restfulResult);
                    } catch (ParamUnSupportException e) {
                        return new ErrorView(HttpResponseCode.CODE_415);
                    }
                } else {
                    params = execLogicNormal(routerName, request.getValues(), router, restfulResult, threadLocal.get());
                }

                boolean isVoid = router.getMethodInfo().getType().getReturnType().getClassName().equals("void");
                Object seedExcuteItrf = threadLocal.get();
                Object result = ((SeedExcuteItrf) seedExcuteItrf).invokeMethod(router.getMethodInfo().getName(), params);
                if (isVoid) {
                    return null;
                }
                if (result instanceof View) {
                    return (View) result;
                }
                return new DefaultView(result);
            } finally {
                router.getControllerAct().invokeAfter();
                router.invokeAfter();
            }
        }
        return new ErrorView(HttpResponseCode.CODE_404);
    }

    private ThreadLocal<Object> getController(SeedRouter router) {
        ThreadLocal<Object> threadLocal = null;
        String threadKey = router.getClassBean().getSeedClz().getName();
        if (threadMaps.containsKey(threadKey)) {
            threadLocal = threadMaps.get(threadKey);
        } else {
            threadLocal = new ThreadLocal<Object>();
            threadMaps.put(threadKey, threadLocal);
        }
        if (threadLocal.get() == null) {
            try {
                threadLocal.set(router.getClassBean().getSeedClz().newInstance());
                ClassBean bean = router.getClassBean();
                for (ClassBean.FieldInfo fieldInfo : bean.getField()) {
                    Inject inject = fieldInfo.getField().getAnnotation(Inject.class);
                    if (inject != null) {
                        try {
                            ClassLoader loader = router.getClassBean().getSeedClz().getClassLoader();
                            Class context = loader.loadClass(Context.class.getName());
                            Method method = context.getMethod("get", Class.class);
                            Object o = method.invoke(null, fieldInfo.getField().getType());
                            if (o == null) {
                                method = context.getMethod("getComponent", String.class);
                                method.setAccessible(true);
                                o = method.invoke(null, fieldInfo.getField().getType().getName());
                            }
                            fieldInfo.getField().set(threadLocal.get(), o);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return threadLocal;
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
            } else if (restfulResult.containsKey(localVar.getName())) {
                value = restfulResult.get(localVar.getName());
            }
            params[i] = value;
        }
        return params;
    }

    private Object[] execLogicNormal(String routerName, Map<String, Object> values, SeedRouter router, HashMap<String, String> restfulResult, Object controller) {
        Object[] params = new Object[router.getMethodInfo().getArgs().length];
        HashMap<String, Integer> sorts = router.getMethodInfo().getArgsSort();
        Map<Integer, SeedExcuteItrf> os = new HashMap<Integer, SeedExcuteItrf>();
        for (Iterator<String> it = router.getArgMapped().keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            ClassBean classBean = router.getArgsMapped(key);
            if (classBean != null) {
                ArgInvoke.invokeObjectArgs(values, os, key, classBean);
            }

            if (router.getMethodInfo().getArgsSort().containsKey(key)) {
                Integer index = router.getMethodInfo().getArgsSort().get(key);
                ClassBean.MethodInfo.LocalVar var = router.getMethodInfo().getLocalVars().get(index);
                ClassLoader classLoader = controller.getClass().getClassLoader();

                Class clz = null;
                try {
                    clz = classLoader.loadClass(var.getType().getClassName());
                    if ((Number.class.isAssignableFrom(clz) || String.class.isAssignableFrom(clz))) {
                        ArgInvoke.invokeNormalArgs(values, params, sorts, key, var);
                        continue;
                    } else if (Collection.class.isAssignableFrom(clz) && var.getSignatureTypes().size() == 2) {
                        ArgInvoke.invokeCollectionArgs(values, params, sorts, key, var, classLoader, clz);
                        continue;
                    }
                } catch (ClassNotFoundException e) {
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
        if (os.size() > 0) {
            for (Iterator<Integer> it = os.keySet().iterator(); it.hasNext(); ) {
                Integer hashCode = it.next();
                SeedExcuteItrf exe = os.get(hashCode);
                String name = exe.getClass().getSuperclass().getName();
                for (ClassBean.MethodInfo.LocalVar localVar : router.getMethodInfo().getLocalVars()) {
                    if (localVar.getType().getClassName().equals(name)) {
                        params[sorts.get(localVar.getName())] = (exe);
                        break;
                    }
                }
            }
        }
        if (restfulResult.size() > 0) {
            for (Iterator<String> it = restfulResult.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                params[sorts.get(key)] = (restfulResult.get(key));
            }
        }
        return params;
    }


    public void setWebHtml(String webHtml) {
        SeedWeb.WEB_HTML_PATH = webHtml.replace(".", "/");
        RESOURCE_MAPPING.putAll(ParamsUtil.getResourceMapping(loader, webHtml));
    }

    public void setWebPublic(String webPublic) {
        Map<String, String> values = Utils.spliteParams(webPublic);
        for (Iterator<String> it = values.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = values.get(key);
            SeedPath path = new SeedPath(key, value, loader);
            publicPaths.put(key.toUpperCase(), path);

            RESOURCE_MAPPING.putAll(ParamsUtil.getResourceMapping(loader, path.getPath()));
        }
    }

    public void setDatabase(String activeRecord, String driver, String jdbcUrl, String userName, String passWord, String databaseName, String datasourceClass, String host, String openurl) {
        try {
            if (Integer.parseInt(activeRecord) == 1) {
                HikariConfig config = new HikariConfig();
                if (jdbcUrl != null) {
                    config.setJdbcUrl(jdbcUrl);
                }
                if (driver != null) {
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
                } else {
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
                Class context = loader.loadClass(Context.class.getName());
                Method method = context.getMethod("add", Object.class);
                method.invoke(null, database);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }
}
