package com.opdar.framework.aop;

import com.opdar.framework.asm.*;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * Created by 俊帆 on 2015/12/24.
 */
public class ResourceInvoke implements Opcodes {


    public static Class<?> create(String mapping, String path, boolean isClassPath) {

        try {
            String simpleName = "_" + UUID.randomUUID().toString().replaceAll("-", "");
            String className = "com/opdar/framework/web/resource/" + simpleName;

            ClassWriter cw = new ClassWriter(0);
            FieldVisitor fv;
            MethodVisitor mv;
            AnnotationVisitor av0;

            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);


            {
                av0 = cw.visitAnnotation("Lcom/opdar/framework/web/anotations/Controller;", true);
                av0.visit("value", "/" + mapping + "/");
                av0.visitEnd();
            }

            {
                fv = cw.visitField(ACC_PRIVATE, "isClassPath", "Z", null, null);
                fv.visitEnd();
            }
            {
                fv = cw.visitField(ACC_PUBLIC, "pathMappings", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", null);
                fv.visitEnd();
            }
            {
                fv = cw.visitField(0, "file", "Ljava/io/File;", null, null);
                fv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(l0, l1, l2, "java/lang/NullPointerException");
                Label l3 = new Label();
                mv.visitTryCatchBlock(l0, l1, l3, "java/io/IOException");
                Label l4 = new Label();
                mv.visitLabel(l4);
                mv.visitLineNumber(30, l4);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                Label l5 = new Label();
                mv.visitLabel(l5);
                mv.visitLineNumber(26, l5);
                mv.visitVarInsn(ALOAD, 0);
                if (isClassPath) {
                    mv.visitInsn(ICONST_1);
                } else {
                    mv.visitInsn(ICONST_0);
                }
                mv.visitFieldInsn(PUTFIELD, className, "isClassPath", "Z");
                Label l6 = new Label();
                mv.visitLabel(l6);
                mv.visitLineNumber(27, l6);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/util/HashMap");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                mv.visitFieldInsn(PUTFIELD, className, "pathMappings", "Ljava/util/Map;");
                Label l7 = new Label();
                mv.visitLabel(l7);
                mv.visitLineNumber(31, l7);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "isClassPath", "Z");
                Label l8 = new Label();
                mv.visitJumpInsn(IFEQ, l8);
                mv.visitLabel(l0);
                mv.visitLineNumber(33, l0);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getContextClassLoader", "()Ljava/lang/ClassLoader;", false);
                mv.visitVarInsn(ASTORE, 1);
                Label l9 = new Label();
                mv.visitLabel(l9);
                mv.visitLineNumber(34, l9);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(path);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/ClassLoader", "getResource", "(Ljava/lang/String;)Ljava/net/URL;", false);
                mv.visitVarInsn(ASTORE, 2);
                Label l10 = new Label();
                mv.visitLabel(l10);
                mv.visitLineNumber(35, l10);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "getProtocol", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 3);
                Label l11 = new Label();
                mv.visitLabel(l11);
                mv.visitLineNumber(36, l11);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitLdcInsn("jar");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                mv.visitJumpInsn(IFEQ, l1);
                Label l12 = new Label();
                mv.visitLabel(l12);
                mv.visitLineNumber(37, l12);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(path);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/ClassLoader", "getResource", "(Ljava/lang/String;)Ljava/net/URL;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
                mv.visitTypeInsn(CHECKCAST, "java/net/JarURLConnection");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/net/JarURLConnection", "getJarFile", "()Ljava/util/jar/JarFile;", false);
                mv.visitVarInsn(ASTORE, 4);
                Label l13 = new Label();
                mv.visitLabel(l13);
                mv.visitLineNumber(38, l13);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/jar/JarFile", "entries", "()Ljava/util/Enumeration;", false);
                mv.visitVarInsn(ASTORE, 5);
                Label l14 = new Label();
                mv.visitLabel(l14);
                mv.visitLineNumber(39, l14);
                mv.visitFrame(Opcodes.F_FULL, 6, new Object[]{className, "java/lang/ClassLoader", "java/net/URL", "java/lang/String", "java/util/jar/JarFile", "java/util/Enumeration"}, 0, new Object[]{});
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Enumeration", "hasMoreElements", "()Z", true);
                mv.visitJumpInsn(IFEQ, l1);
                Label l15 = new Label();
                mv.visitLabel(l15);
                mv.visitLineNumber(40, l15);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Enumeration", "nextElement", "()Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/util/jar/JarEntry");
                mv.visitVarInsn(ASTORE, 6);
                Label l16 = new Label();
                mv.visitLabel(l16);
                mv.visitLineNumber(41, l16);
                mv.visitVarInsn(ALOAD, 6);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/jar/JarEntry", "getName", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 7);
                Label l17 = new Label();
                mv.visitLabel(l17);
                mv.visitLineNumber(42, l17);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitLdcInsn(path + "/");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "indexOf", "(Ljava/lang/String;)I", false);
                Label l18 = new Label();
                mv.visitJumpInsn(IFNE, l18);
                Label l19 = new Label();
                mv.visitLabel(l19);
                mv.visitLineNumber(43, l19);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitLdcInsn(path + "/");
                mv.visitLdcInsn("");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 7);
                Label l20 = new Label();
                mv.visitLabel(l20);
                mv.visitLineNumber(44, l20);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "pathMappings", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 7);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toUpperCase", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitInsn(POP);
                mv.visitLabel(l18);
                mv.visitLineNumber(46, l18);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitJumpInsn(GOTO, l14);
                mv.visitLabel(l1);
                mv.visitLineNumber(52, l1);
                mv.visitFrame(Opcodes.F_FULL, 1, new Object[]{className}, 0, new Object[]{});
                Label l21 = new Label();
                mv.visitJumpInsn(GOTO, l21);
                mv.visitLabel(l2);
                mv.visitLineNumber(48, l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/NullPointerException"});
                mv.visitVarInsn(ASTORE, 1);
                Label l22 = new Label();
                mv.visitLabel(l22);
                mv.visitLineNumber(49, l22);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/NullPointerException", "printStackTrace", "()V", false);
                Label l23 = new Label();
                mv.visitLabel(l23);
                mv.visitLineNumber(52, l23);
                mv.visitJumpInsn(GOTO, l21);
                mv.visitLabel(l3);
                mv.visitLineNumber(50, l3);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/io/IOException"});
                mv.visitVarInsn(ASTORE, 1);
                Label l24 = new Label();
                mv.visitLabel(l24);
                mv.visitLineNumber(51, l24);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/IOException", "printStackTrace", "()V", false);
                Label l25 = new Label();
                mv.visitLabel(l25);
                mv.visitLineNumber(52, l25);
                mv.visitJumpInsn(GOTO, l21);
                mv.visitLabel(l8);
                mv.visitLineNumber(54, l8);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getContextClassLoader", "()Ljava/lang/ClassLoader;", false);
                mv.visitLdcInsn("/");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/ClassLoader", "getResource", "(Ljava/lang/String;)Ljava/net/URL;", false);
                mv.visitVarInsn(ASTORE, 1);
                Label l26 = new Label();
                mv.visitLabel(l26);
                mv.visitLineNumber(55, l26);
                mv.visitTypeInsn(NEW, "java/io/File");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "getPath", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "getParentFile", "()Ljava/io/File;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "getParentFile", "()Ljava/io/File;", false);
                mv.visitVarInsn(ASTORE, 2);
                Label l27 = new Label();
                mv.visitLabel(l27);
                mv.visitLineNumber(56, l27);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/io/File");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitLdcInsn(path+"/");
                mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/io/File;Ljava/lang/String;)V", false);
                mv.visitFieldInsn(PUTFIELD, className, "file", "Ljava/io/File;");
                Label l28 = new Label();
                mv.visitLabel(l28);
                mv.visitLineNumber(57, l28);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "pathMappings", "Ljava/util/Map;");
                mv.visitLdcInsn("");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "file", "Ljava/io/File;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "getPath", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKESTATIC, "com/opdar/framework/utils/ResourceUtils", "findMapping", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/Map;", false);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "putAll", "(Ljava/util/Map;)V", true);
                mv.visitLabel(l21);
                mv.visitLineNumber(59, l21);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitInsn(RETURN);
                Label l29 = new Label();
                mv.visitLabel(l29);
                mv.visitLocalVariable("entry", "Ljava/util/jar/JarEntry;", null, l16, l18, 6);
                mv.visitLocalVariable("name", "Ljava/lang/String;", null, l17, l18, 7);
                mv.visitLocalVariable("jar", "Ljava/util/jar/JarFile;", null, l13, l1, 4);
                mv.visitLocalVariable("entries", "Ljava/util/Enumeration;", "Ljava/util/Enumeration<Ljava/util/jar/JarEntry;>;", l14, l1, 5);
                mv.visitLocalVariable("loader", "Ljava/lang/ClassLoader;", null, l9, l1, 1);
                mv.visitLocalVariable("resource", "Ljava/net/URL;", null, l10, l1, 2);
                mv.visitLocalVariable("protocol", "Ljava/lang/String;", null, l11, l1, 3);
                mv.visitLocalVariable("e", "Ljava/lang/NullPointerException;", null, l22, l23, 1);
                mv.visitLocalVariable("e", "Ljava/io/IOException;", null, l24, l25, 1);
                mv.visitLocalVariable("url", "Ljava/net/URL;", null, l26, l21, 1);
                mv.visitLocalVariable("webinf", "Ljava/io/File;", null, l27, l21, 2);
                mv.visitLocalVariable("this", "LResourcesController;", null, l4, l29, 0);
                mv.visitMaxs(5, 8);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "res", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
                {
                    av0 = mv.visitAnnotation("Lcom/opdar/framework/web/anotations/Router;", true);
                    av0.visit("value", "#{res}");
                    av0.visitEnd();
                }
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(63, l0);
                mv.visitTypeInsn(NEW, "javax/activation/MimetypesFileTypeMap");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "javax/activation/MimetypesFileTypeMap", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "javax/activation/MimetypesFileTypeMap", "getContentType", "(Ljava/lang/String;)Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 2);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLineNumber(64, l1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "isClassPath", "Z");
                Label l2 = new Label();
                mv.visitJumpInsn(IFEQ, l2);
                Label l3 = new Label();
                mv.visitLabel(l3);
                mv.visitLineNumber(65, l3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "pathMappings", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true);
                Label l4 = new Label();
                mv.visitJumpInsn(IFEQ, l4);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "pathMappings", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/lang/String");
                mv.visitVarInsn(ASTORE, 1);
                mv.visitLabel(l4);
                mv.visitLineNumber(66, l4);
                mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
                mv.visitTypeInsn(NEW, "com/opdar/framework/web/views/FileView");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getContextClassLoader", "()Ljava/lang/ClassLoader;", false);
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn(path + "/");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/ClassLoader", "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;", false);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ACONST_NULL);
                mv.visitMethodInsn(INVOKESPECIAL, "com/opdar/framework/web/views/FileView", "<init>", "(Ljava/io/InputStream;Ljava/lang/String;Lcom/opdar/framework/web/views/FileView$FileReadListener;)V", false);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l2);
                mv.visitLineNumber(68, l2);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "pathMappings", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true);
                Label l5 = new Label();
                mv.visitJumpInsn(IFEQ, l5);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "pathMappings", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/lang/String");
                mv.visitVarInsn(ASTORE, 1);
                mv.visitLabel(l5);
                mv.visitLineNumber(69, l5);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitTypeInsn(NEW, "java/io/File");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "file", "Ljava/io/File;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/io/File;Ljava/lang/String;)V", false);
                mv.visitVarInsn(ASTORE, 3);
                Label l6 = new Label();
                mv.visitLabel(l6);
                mv.visitLineNumber(70, l6);
                mv.visitTypeInsn(NEW, "com/opdar/framework/web/views/FileView");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ACONST_NULL);
                mv.visitMethodInsn(INVOKESPECIAL, "com/opdar/framework/web/views/FileView", "<init>", "(Ljava/io/File;Ljava/lang/String;Lcom/opdar/framework/web/views/FileView$FileReadListener;)V", false);
                mv.visitInsn(ARETURN);
                Label l7 = new Label();
                mv.visitLabel(l7);
                mv.visitLocalVariable("file_", "Ljava/io/File;", null, l6, l7, 3);
                mv.visitLocalVariable("this", "LResourcesController;", null, l0, l7, 0);
                mv.visitLocalVariable("res", "Ljava/lang/String;", null, l0, l7, 1);
                mv.visitLocalVariable("contentType", "Ljava/lang/String;", null, l1, l7, 2);
                mv.visitMaxs(5, 4);
                mv.visitEnd();
            }
            cw.visitEnd();

            Class<?> clz = SeedInvoke.define(className, cw.toByteArray());
            ByteArrayInputStream bais = new ByteArrayInputStream(cw.toByteArray());
            SeedInvoke.init(clz, bais);
            return clz;

        } catch (Exception e) {

        }
        return null;
    }

    public static void main(String[] args) {
        try {
            create("pub", "pub", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
