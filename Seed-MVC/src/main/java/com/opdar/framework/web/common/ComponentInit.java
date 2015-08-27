package com.opdar.framework.web.common;

import com.opdar.framework.utils.ThreadLocalUtils;
import com.opdar.framework.web.anotations.Component;
import com.opdar.framework.web.anotations.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 俊帆 on 2015/8/21.
 */
public class ComponentInit {
    private ThreadLocal<Object> componentLoad = new ThreadLocal<Object>();
    private Class<?> component;
    private String[] componentNames;
    private Object[] initArgs;
    private List<ComponentMethod> methods = new LinkedList<ComponentMethod>();
    private static final String COMPONENT_KEY = "COMPONENT_KEY";
    public ComponentInit(Class<?> component) {
        this.component = component;
        componentNames = new String[component.getInterfaces().length+1];
        componentNames[0] = component.getName();
        for(int i=1;i<componentNames.length;i++){
            componentNames[i] = component.getInterfaces()[i].getName();
        }
    }

    public void remove(){
        ThreadLocalUtils.clearThreadLocals(COMPONENT_KEY,componentLoad);
    }

    public String[] getComponentName() {
        return componentNames;
    }

    public Object getComponentObject() {
        ThreadLocalUtils.record(COMPONENT_KEY);

        ClassLoader loader = component.getClassLoader();
        Class context = null;
        Method getComponentMethod = null,getMethod = null;
        try {
            context = loader.loadClass(Context.class.getName());
            getComponentMethod = context.getMethod("getComponent", String.class);
            getComponentMethod.setAccessible(true);
            getMethod = context.getMethod("get", String.class);
            getMethod.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object o = componentLoad.get();
        if (o == null) {
            try {
                if (initArgs == null) {
                    o = component.newInstance();
                } else {
                    Class<?>[] co = new Class<?>[initArgs.length];
                    for(int i=0;i<initArgs.length;i++){
                        co[i] = initArgs[i].getClass();
                    }
                    Constructor<?> construtor = component.getConstructor(co);
                    construtor.setAccessible(true);
                    o = construtor.newInstance(initArgs);
                }
                for(ComponentMethod method:methods){
                    if(method.getArgs()!= null){
                        Class<?>[] co = new Class<?>[method.getArgs().length];
                        for(int i=0;i<method.getArgs().length;i++){
                            co[i] = method.getArgs()[i].getClass();
                        }
                        Method method1 = component.getDeclaredMethod(method.getName(), co);
                        method1.setAccessible(true);
                        method1.invoke(o,method.getArgs());
                    }
                }
                for(Field field:component.getDeclaredFields()){
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    Inject inject = field.getAnnotation(Inject.class);
                    if(inject != null){
                        Object o2 = getComponentMethod.invoke(null,fieldType.getName());
                        if(o2 == null){
                            o2 = getMethod.invoke(null,fieldType.getName());
                        }
                        field.set(o,o2);
                    }
                }
                componentLoad.set(o);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

}
