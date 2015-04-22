package com.opdar.framework.aop;

import com.opdar.framework.aop.base.SeedType;
import com.opdar.framework.asm.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * Created by Jeffrey on 2015/4/8.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedWeakClassloader extends URLClassLoader implements Opcodes
{
    private static HashMap<String, java.lang.reflect.Type> types = new HashMap<String, java.lang.reflect.Type>();

    public SeedWeakClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Class<?> definedClass(String sig){
        String className = null;
        byte[] code = new byte[0];
        try {
            className = String.format("com/opdar/framework/types/__Seed_R_%s_Copy_%s",sig.replace(".","_").replace("/","_").replace(";","_").replace("<","_").replace(">","_"), Thread.currentThread().getId());
            code = productTypeClass(className, sig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * 从文件加载类
         */
        Class seedClass = defineClass(className.replace("/","."),
                code,
                0,
                code.length);
        return seedClass;
    }

    public static java.lang.reflect.Type getType(String sig){

        java.lang.reflect.Type type = null;
        if(types.containsKey(sig)){
            type = types.get(sig);
        }else{
            SeedWeakClassloader classloader = new SeedWeakClassloader(new URL[]{},Thread.currentThread().getContextClassLoader());
            WeakReference<SeedWeakClassloader> classLoaderWr = new WeakReference<SeedWeakClassloader>(classloader);
            classloader = null;
            try {
                SeedType seedType = (SeedType) classLoaderWr.get().definedClass(sig).newInstance();
                type = seedType.getType();
                types.put(sig,type);
                classLoaderWr.clear();
                classLoaderWr = null;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return type;
    }


    public static byte[] productTypeClass(String className,String sig) throws Exception {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        String seedType = SeedType.class.getName().replace(".","/");
        String typeName = com.opdar.framework.aop.base.TypeReference.class.getName().replace(".","/");
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, "L"+typeName+"<"+sig+">;>;L"+seedType+";", typeName, new String[]{seedType});

        {
            fv = cw.visitField(ACC_PUBLIC, "_type", "Ljava/lang/reflect/Type;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, typeName, "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getGenericSuperclass", "()Ljava/lang/reflect/Type;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, "java/lang/Class");
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("Internal error: TypeReference constructed without actual type information");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{className, "java/lang/reflect/Type"}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/reflect/ParameterizedType");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/ParameterizedType", "getActualTypeArguments", "()[Ljava/lang/reflect/Type;", true);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitFieldInsn(PUTFIELD, className, "_type", "Ljava/lang/reflect/Type;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getType", "()Ljava/lang/reflect/Type;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "_type", "Ljava/lang/reflect/Type;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

}
