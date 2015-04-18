package com.opdar.framework.utils;

import com.opdar.framework.asm.*;
import com.opdar.framework.utils.FieldModel;
import com.opdar.framework.asm.*;
import com.opdar.framework.utils.MethodModel;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ParamsUtil {
    public static Set<Class<?>> getClasses(String pack) {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(
                    packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath,
                            recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection())
                                .getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx)
                                            .replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    if (name.endsWith(".class")
                                            && !entry.isDirectory()) {
                                        String className = name.substring(
                                                packageName.length() + 1, name
                                                        .length() - 6);
                                        try {
                                            classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的�?有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Set<FieldModel> getField(final Object obj) {
        return getField(obj,true);
    }

    public static Set<FieldModel> getField(Class<?> tClass) {
        try {
            return getField(tClass.newInstance(),false);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<FieldModel> getField(final Object obj, final boolean ignoreNull) {
        ClassReader cr = null;
        final Class<?> clz = obj.getClass();
        try {
            String clzName = clz.getName().replace(".","/").concat(".class");
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(clzName);
            cr = new ClassReader(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Set<FieldModel> fields = new HashSet<FieldModel>();
        cr.accept(new ClassVisitor(Opcodes.ASM4) {

            @Override
            public void visit(int version, int access, String name,
                              String signature, String superName, String[] interfaces) {
                // TODO Auto-generated method stub
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public FieldVisitor visitField(int access, String name,
                                           String desc, String signature, Object value) {
                // TODO Auto-generated method stub
                try {
                    Type type = Type.getType(desc);
                    if(type.getSort() >1 &&type.getSort()<9 || type.getSort() == 10 ){
                        Field field = clz.getDeclaredField(name);
                        field.setAccessible(true);
                        Object value2 = field.get(obj);
                        FieldModel fieldModel = null;
                        if(value2 != null && ignoreNull){
                            fieldModel = new FieldModel();
                            fieldModel.setName(name);
                            fieldModel.setField(field);
                            fieldModel.setType(type);
                            fieldModel.setValue(value2);
                        }else if(value !=null && ignoreNull){
                            fieldModel = new FieldModel();
                            fieldModel.setName(name);
                            fieldModel.setField(field);
                            fieldModel.setType(type);
                            fieldModel.setValue(value);
                        }else if(!ignoreNull){
                            fieldModel = new FieldModel();
                            fieldModel.setName(name);
                            fieldModel.setField(field);
                            fieldModel.setType(type);
                        }
                        if(fieldModel != null){
                            fields.add(fieldModel);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return super.visitField(access, name, desc, signature, value);
            }
        }, 0);
        return fields;
    }

    public static HashMap<String, MethodModel> getMethodWithClass(final Class<?> clz) {
        ClassReader cr = null;
        try {
            String clzName = clz.getName().replace(".","/").concat(".class");
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(clzName);
            cr = new ClassReader(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final HashMap<String, MethodModel> models = new HashMap<String, MethodModel>();
        cr.accept(new ClassVisitor(Opcodes.ASM4) {
            @Override
            public MethodVisitor visitMethod(final int access,
                                             final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                Method[] methods = clz.getDeclaredMethods();
                final Type[] args = Type.getArgumentTypes(desc);
                Method m = null;
                for(Method method:methods){
                    if(name.equals(method.getName()) && sameType(args, method.getParameterTypes())){
                        m = method;
                        break;
                    }
                }
                if(m == null)return null;
                final MethodModel methodModel = new MethodModel();
                methodModel.setMethodName(name);
                methodModel.setArgs(args);
                if (Modifier.isStatic(m.getModifiers())) {
                    methodModel.setStaticMethod();
                }
                models.put(name,methodModel);
                MethodVisitor v = super.visitMethod(access, name, desc,
                        signature, exceptions);
                return new MethodVisitor(Opcodes.ASM4, v) {

                    private Type paramType;
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc,
                                                             boolean visible) {
                        // TODO Auto-generated method stub
                        Type type = Type.getType(desc);
                        methodModel.setAnotation(type);
                        return super.visitAnnotation(desc, visible);
                    }

                    @Override
                    public void visitLocalVariable(String name, String desc,
                                                   String signature, Label start, Label end, int index) {
                        int i = index - 1;
                        if (methodModel.isStaticMethod()) {
                            i = index;
                        }
                        if (i >= 0 && i < args.length) {
                            if(paramType != null){
                                methodModel.addParamAnoatations(name,paramType);
                                paramType = null;
                            }
                            Type type = Type.getType(desc);
                            methodModel.setParams(name);
                        }
                        super.visitLocalVariable(name, desc, signature, start,
                                end, index);
                    }
                    @Override
                    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                        Type type = Type.getType(desc);
                        paramType = type;
                        return super.visitParameterAnnotation(parameter, desc, visible);
                    }

                };
            }
        }, 0);
        return models;
    }

    private static boolean sameType(Type[] types, Class<?>[] clazzes) {
        if (types.length != clazzes.length) {
            return false;
        }

        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }


}
