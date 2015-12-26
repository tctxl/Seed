package com.opdar.framework.template.parser;

import com.opdar.framework.asm.ClassWriter;
import com.opdar.framework.asm.MethodVisitor;
import com.opdar.framework.asm.Opcodes;
import com.opdar.framework.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 俊帆 on 2015/12/26.
 */
public class TemplateCache implements Opcodes {
    String get(){
        StringBuilder builder = new StringBuilder();
        builder.append("aaa").append("bbb");
        List list = new LinkedList();
        for(int i=0;i<list.size();i++){
            String a = (String)list.get(i);

        }
        return "";
    }

    public void build(){

    }

    public static void main(String[] args) {
        ClassWriter cw = new ClassWriter(0);
        String className = "com/opdar/framework/template/cache/" + UUID.randomUUID().toString().replaceAll("-","");
        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        Utils.save(cw.toByteArray());
    }
}
