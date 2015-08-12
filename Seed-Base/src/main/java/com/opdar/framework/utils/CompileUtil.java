package com.opdar.framework.utils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by 俊帆 on 2015/8/7.
 */
public class CompileUtil {

    public static void compile(File classPath, Writer writer, String name) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject fileObject = new JavaStringObject(name, writer.toString());
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, null, null, Arrays.asList("-d", classPath.getAbsolutePath()), null, Arrays.asList(fileObject));
        boolean success = task.call();
        if (!success) {
            System.out.println("编译失败");
        } else {
            System.out.println("编译成功");
        }
    }

    public static void main(String[] args) {
//        String s = "public class Taq{  }";
//        File classPath = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()).getParentFile();
//        System.out.println(classPath);
//        StringWriter writer = new StringWriter();
//        writer.write(s);
//        writer.flush();
//        File temp = new File(classPath,"temp");
//        if(!temp.exists()){
//            temp.mkdirs();
//        }
//        compile(temp,writer, "Taq");
        try {
            int i = NumberFormat.getInstance().parse("1+2+3").byteValue();
            System.out.println(i);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static class JavaStringObject extends SimpleJavaFileObject {
        private String code;

        public JavaStringObject(String name, String code) {
            super(URI.create(name + ".java"), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException {
            return code;
        }
    }
}
