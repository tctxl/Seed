package com.opdar.framework.web;

import com.opdar.framework.aop.SeedWeakClassloader;
import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.db.impl.BaseDatabase;
import com.opdar.framework.db.impl.DaoMap;
import com.opdar.framework.db.impl.OnDataSourceCloseListener;
import com.opdar.framework.utils.ParamsUtil;
import com.opdar.framework.utils.PrimaryUtil;
import com.opdar.framework.utils.ThreadLocalUtils;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.common.*;
import com.opdar.framework.web.exceptions.ParamUnSupportException;
import com.opdar.framework.web.interfaces.HttpConvert;
import com.opdar.framework.web.interfaces.View;
import com.opdar.framework.web.parser.FormParser;
import com.opdar.framework.web.parser.HttpParser;
import com.opdar.framework.web.utils.ControllerInvoke;
import com.opdar.framework.web.utils.RestfulInvoke;
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
    private final Log log = LogFactory.getLog("SeedWeb");

    private ClassLoader loader = Thread.currentThread().getContextClassLoader();

    //所有路由
    private static final HashMap<String, ThreadLocal<Object>> threadMaps = new HashMap<String, ThreadLocal<Object>>();
    //转换器
    private static final HashMap<String, HttpConvert> converts = new HashMap<String, HttpConvert>();
    //输入流解析器
    private static final Map<String, HttpParser> parsers = new HashMap<String, HttpParser>();
    //默认页
    private static final HashSet<String> defaultPages = new HashSet<String>();
    public static String WEB_HTML_PATH = "";
    public static final HashMap<String, SeedPath> publicPaths = new HashMap<String, SeedPath>();
    public static Map<String, String> RESOURCE_MAPPING = new HashMap<String, String>();
    private static final String HTTP_THREAD_KEY = "HTTP_THREAD_KEY";

    ControllerInvoke controllerInvoke = new ControllerInvoke();

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
        return null;
    }


    public void scanController(String packageName) {
        scanController(packageName, true, null);
    }

    /**
     * 扫描控制器并生成路由
     *
     * @param packageName 包名
     */
    public void scanController(String packageName, boolean isClear, String perfix) {
        synchronized (this) {
            if (isClear) controllerInvoke = new ControllerInvoke();
            controllerInvoke.scan(packageName, perfix, loader);
        }
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
                if (ret != null && (!(ret instanceof Boolean) || (Boolean) ret)) {
                    return (View) ret;
                }
                ret = router.invokeBefore();
                if (ret != null && (!(ret instanceof Boolean) || (Boolean) ret)) {
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
        for (Iterator<String> it = values.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            ClassBean classBean = router.getArgsMapped(key);
            if (!router.getMethodInfo().getArgsSort().containsKey(key)) continue;
            Integer index = router.getMethodInfo().getArgsSort().get(key);
            ClassBean.MethodInfo.LocalVar var = router.getMethodInfo().getLocalVars().get(index);
            ClassLoader classLoader = controller.getClass().getClassLoader();
            try {
                Class clz = classLoader.loadClass(var.getType().getClassName());
                if (classBean == null && (Number.class.isAssignableFrom(clz) || String.class.isAssignableFrom(clz))) {
                    if (sorts.containsKey(key))
                        params[sorts.get(key)] = PrimaryUtil.cast(values.get(key), var.getType());
                    continue;
                } else if (Collection.class.isAssignableFrom(clz) && var.getSignatureTypes().size() == 2) {
                    String className = var.getSignatureTypes().get(0).replace("/", ".");
                    Class genicClz = classLoader.loadClass(className);

                    if (Number.class.isAssignableFrom(genicClz) || String.class.isAssignableFrom(genicClz)) {
                        Collection list = null;
                        if (clz.isInterface()) {
                            list = new LinkedList();
                        } else {
                            list = (Collection) clz.newInstance();
                        }
                        Collection result = (Collection) values.get(key);
                        for (Iterator it2 = result.iterator(); it2.hasNext(); ) {
                            Object val = it2.next();
                            list.add(PrimaryUtil.cast(val, genicClz));
                        }
                        params[sorts.get(key)] = list;
                        continue;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
        if (restfulResult.size() > 0) {
            for (Iterator<String> it = restfulResult.keySet().iterator(); it.hasNext(); ) {
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
