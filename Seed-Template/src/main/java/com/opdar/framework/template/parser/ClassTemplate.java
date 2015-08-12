package com.opdar.framework.template.parser;

import com.opdar.framework.asm.*;
import com.opdar.framework.template.base.Variable;
import com.opdar.framework.utils.Utils;

/**
 * Created by 俊帆 on 2015/8/10.
 */
public class ClassTemplate implements Opcodes {
    public byte[] get(){

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;
        String className = "ab331aaaa5aa32s5ffrhger";
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,className , null, "java/lang/Object", null);

        //初始化打印栈
        {
            fv = cw.visitField(ACC_PUBLIC, "stack", "Ljava/util/Stack;", "Ljava/util/Stack<Ljava/lang/String;>;", null);
            fv.visitEnd();
        }
        //构造方法初始化
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(7, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(8, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/Stack");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/Stack", "<init>", "()V", false);
            mv.visitFieldInsn(PUTFIELD, className, "stack", "Ljava/util/Stack;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "get", "(I)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("t1");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitLdcInsn("t2");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitLdcInsn("t2");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitLdcInsn("t2");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "Test", "map", "Ljava/util/HashMap;");
            mv.visitLdcInsn("k");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "Test", "map", "Ljava/util/HashMap;");
            mv.visitLdcInsn("k");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "Test", "map", "Ljava/util/HashMap;");
            mv.visitLdcInsn("k");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    public static void main(String[] args) {
        Utils.save(new ClassTemplate().get());
    }
}
