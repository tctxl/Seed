package com.opdar.framework.aop;

import com.opdar.framework.aop.base.ClassBean;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.asm.*;
import com.opdar.framework.asm.signature.SignatureReader;
import com.opdar.framework.asm.signature.SignatureVisitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.*;
import java.util.*;

/**
 * Created by Jeffrey on 2015/4/8.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedInvoke extends URLClassLoader implements Opcodes
{
    private static String _iter = SeedExcuteItrf.class.getName().replaceAll("\\.", "/");
    private static HashMap<Class<?>,ClassBean> beanSymbols = new HashMap<Class<?>, ClassBean>();
    static SeedInvoke loader = new SeedInvoke();
    public SeedInvoke() {
        super(new URL[]{},Thread.currentThread().getContextClassLoader());
    }

    public static HashMap<Class<?>, ClassBean> getBeanSymbols() {
        return beanSymbols;
    }

    public static void init(final Class<?> clz){
        if(beanSymbols.containsKey(clz))return;
        ClassReader cr = null;
        ClassBean cb = null;
        beanSymbols.put(clz, cb = new ClassBean());
        try {
            String clzName = clz.getName().replace(".","/").concat(".class");
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(clzName);
            cr = new ClassReader(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(new ClassVisitor(Opcodes.ASM4) {

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                final ClassBean.AnotationInfo anotationInfo = new ClassBean.AnotationInfo();
                beanSymbols.get(clz).setAnotation(anotationInfo);
                anotationInfo.setDesc(desc);
                anotationInfo.setType(Type.getType(desc));
                return new AnnotationVisitor(ASM4) {
                    @Override
                    public void visit(String name, Object value) {
                        ClassBean.AnotationInfo.AnotationValue value1 = new ClassBean.AnotationInfo.AnotationValue();
                        value1.setName(name);
                        value1.setValue(value);
                        anotationInfo.setValue(value1);
                        super.visit(name, value);
                    }
                };
            }


            @Override
            public void visit(int version, int access, String name,
                              String signature, String superName, String[] interfaces) {
                beanSymbols.get(clz).setVersion(version);
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(final int access,
                                             final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                final ClassBean.MethodInfo methodInfo = new ClassBean.MethodInfo();
                beanSymbols.get(clz).setMethods(methodInfo);
                methodInfo.setName(name);
                methodInfo.setDesc(desc);
                methodInfo.setType(Type.getType(desc));
                methodInfo.setArgs(args);
                methodInfo.setAccess(access);
                return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc,
                        signature, exceptions)) {
                    Map<Integer,ClassBean.AnotationInfo> paramAnnotations = new HashMap<Integer, ClassBean.AnotationInfo>();

                    @Override
                    public void visitParameter(String name, int access) {
                        super.visitParameter(name, access);
                    }

                    @Override
                    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                        final ClassBean.AnotationInfo anotationInfo = new ClassBean.AnotationInfo();
                        anotationInfo.setDesc(desc);
                        anotationInfo.setType(Type.getType(desc));
                        paramAnnotations.put(parameter,anotationInfo);
                        return new AnnotationVisitor(ASM4) {
                            @Override
                            public void visit(String name, Object value) {
                                ClassBean.AnotationInfo.AnotationValue value1 = new ClassBean.AnotationInfo.AnotationValue();
                                value1.setName(name);
                                value1.setValue(value);
                                anotationInfo.setValue(value1);
                                super.visit(name, value);
                            }
                        };
                    }
                    @Override
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        if(index>0){
                            ClassBean.MethodInfo.LocalVar var = new ClassBean.MethodInfo.LocalVar();
                            var.setName(name);
                            if(paramAnnotations.containsKey(index-1)){
                                var.setAnnotations(paramAnnotations.get(index-1));
                            }
                            methodInfo.setArgsSort(name);
                            var.setDesc(desc);
                            final LinkedList<String> signatureTypes = new LinkedList<String>();
                            var.setSignatureTypes(signatureTypes);
                            if(signature != null){
                                var.setSignature(signature);
                                SignatureReader signatureReader = new SignatureReader(signature);
                                signatureReader.accept(new SignatureVisitor(ASM4) {
                                    @Override
                                    public void visitClassType(String name) {
                                        signatureTypes.add(0, name);
                                        super.visitClassType(name);
                                    }
                                });
                            }

                            var.setType(Type.getType(desc));
                            var.setIndex(index);
                            methodInfo.setLocalVars(var);
                        }
                        super.visitLocalVariable(name, desc, signature, start, end, index);
                    }

                    @Override
                    public AnnotationVisitor visitAnnotation(String desc,
                                                             boolean visible) {
                        // TODO Auto-generated method stub
                        final ClassBean.AnotationInfo anotationInfo = new ClassBean.AnotationInfo();
                        methodInfo.setAnotation(anotationInfo);
                        anotationInfo.setDesc(desc);
                        anotationInfo.setType(Type.getType(desc));
                        return new AnnotationVisitor(ASM4) {
                            @Override
                            public void visit(String name, Object value) {
                                ClassBean.AnotationInfo.AnotationValue value1 = new ClassBean.AnotationInfo.AnotationValue();
                                value1.setName(name);
                                value1.setValue(value);
                                anotationInfo.setValue(value1);
                                super.visit(name, value);
                            }
                        };
                    }

                };
            }

            //field读取
            @Override
            public FieldVisitor visitField(int access, String name,
                                           String desc, String signature, Object value) {
                // TODO Auto-generated method stub
                try {
                    Type type = Type.getType(desc);
                    if (type.getSort() > 1 && type.getSort() < 9 || type.getSort() == 10) {
                        ClassBean.FieldInfo fieldInfo = new ClassBean.FieldInfo();
                        fieldInfo.setName(name);
                        fieldInfo.setDesc(desc);
                        fieldInfo.setType(type);
                        fieldInfo.setAccess(access);
                        fieldInfo.setDefaultValue(value);
                        beanSymbols.get(clz).setField(fieldInfo);
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                return super.visitField(access, name, desc, signature, value);
            }
        }, 0);
        Class<?> defineClz = definedClass(clz);
        cb.setSeedClz(defineClz);
    }

    public static Class<?> definedClass(Class<?> clazz){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String className = String.format("com/opdar/framework/invoke/__Seed_T_%s_Copy_%s",clazz.getName().replace(".","_"),Thread.currentThread().getId());
        ClassBean c = beanSymbols.get(clazz);
        c.setClassName(className);
        String typeName = clazz.getName().replace(".","/");
        c.setTypeName(typeName);
        cw.visit(c.getVersion(),
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                typeName,
                new String[] {_iter});
        MethodVisitor mv = null;
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(4, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, typeName, "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(6, l1);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        int ln = 11;
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invokeField", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(9, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ASTORE, 3);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(10, l1);

            for(ClassBean.FieldInfo fieldInfo:c.getField()){
                if(Modifier.isStatic(fieldInfo.getAccess())){
                    continue;
                }
                mv.visitLdcInsn(fieldInfo.getName());
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                Label l2 = new Label();
                mv.visitJumpInsn(IFEQ, l2);
                Label l3 = new Label();
                mv.visitLabel(l3);
                mv.visitLineNumber(ln, l3);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitTypeInsn(CHECKCAST, fieldInfo.getType().getClassName().replace(".","/"));
                mv.visitFieldInsn(PUTFIELD, typeName, fieldInfo.getName(), fieldInfo.getDesc());
                mv.visitLabel(l2);
                ln+=2;
                mv.visitLineNumber(ln, l2);
                ln++;
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invokeMethod", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(ln, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ASTORE, 3);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(++ln, l1);

            {
                //定义返回值变量
                mv.visitInsn(ACONST_NULL);
                mv.visitVarInsn(ASTORE, 4);
                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitLineNumber(++ln, l2);
            }
            for(ClassBean.MethodInfo methodInfo:c.getMethods()) {
                if (methodInfo.getName().equals("<init>")) continue;

                if(Modifier.isStatic(methodInfo.getAccess())){
                    continue;
                }
                mv.visitLdcInsn(methodInfo.getName());
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                Label l2 = new Label();
                mv.visitJumpInsn(IFEQ, l2);
                Label l3 = new Label();
                mv.visitLabel(l3);
                mv.visitLineNumber(++ln, l3);
                mv.visitVarInsn(ALOAD, 3);

                for(int i =0;i<methodInfo.getArgs().length;i++){
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitIntInsn(BIPUSH, i);
                    mv.visitInsn(AALOAD);
                    Type type = methodInfo.getArgs()[i];

                    switch (type.getSort()) {
                        case Type.ARRAY:
                            mv.visitTypeInsn(CHECKCAST, type.getDescriptor());
                            break;
                        case Type.OBJECT:
                            mv.visitTypeInsn(CHECKCAST, type.getInternalName());
                            break;
                        default:
                            mv.visitTypeInsn(CHECKCAST, type.getClassName().replace(".","/"));
                            break;
                    }
                }

                mv.visitMethodInsn(INVOKEVIRTUAL, typeName, methodInfo.getName(), methodInfo.getDesc(), false);
                if(Type.getType(methodInfo.getDesc()).getReturnType().getSort() != Type.VOID){
                    mv.visitVarInsn(ASTORE, 4);
                }
                mv.visitLabel(l2);
                mv.visitLineNumber(++ln, l2);
            }
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(5, 4);
            mv.visitEnd();
        }
        cw.visitEnd();
        byte[] code = cw.toByteArray();
        /*
         * 从文件加载类
         */
        Class seedClass = null;
        synchronized (SeedInvoke.class){
            seedClass = loader.defineClass(className.replace("/","."),code,0,code.length);
        }
        return seedClass;
    }

    public static SeedExcuteItrf buildObject(Class clazz) throws Exception
    {
        SeedExcuteItrf obj = null;
        if(!beanSymbols.containsKey(clazz)){
            init(clazz);
        }
        obj = (SeedExcuteItrf) beanSymbols.get(clazz).getSeedClz().newInstance();
        return obj;
    }

    public static java.lang.reflect.Type getType(){
        ClassLoader referent = new ClassLoader() {

        };
        WeakReference<ClassLoader> classLoaderWr = new WeakReference<ClassLoader>(referent);

        java.lang.reflect.Type type = null;
        return type;
    }


}
