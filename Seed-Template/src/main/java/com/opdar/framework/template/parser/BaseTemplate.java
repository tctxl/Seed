package com.opdar.framework.template.parser;

import com.opdar.framework.template.base.ParserDefined;
import com.opdar.framework.template.base.Part;
import com.opdar.framework.template.res.Loader;
import com.opdar.framework.template.utils.TemplateUtil;

import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.HashMap;

/**
 * Created by 俊帆 on 2015/8/3.
 */
public class BaseTemplate implements Template {

    //程序外变量标签
    private String PARAM_LEFT_SYMBOL = "${";
    private String PARAM_RIGHT_SYMBOL = "}";

    //程序段落标签
    private String PROGRAM_LEFT_SYMBOL = "<%";
    private String PROGRAM_RIGHT_SYMBOL = "%>";

    private String content;

    private StringBuilder contentBuilder = new StringBuilder();
    Part part;
    private HashMap globalVars = new HashMap();

    private Loader resourceLoader;

    public BaseTemplate(String content, CharBuffer contentBuffer, Loader loader) {
        this.content = content;
        findProgram(contentBuffer);
        ParserDefined parserDefined = new ParserDefined();
        part = parserDefined.parse(contentBuilder.toString());
        this.resourceLoader = loader;
    }

    @Override
    public String parse(Object object) {
        StringWriter sw = new StringWriter();
        TemplateUtil.part(part, object, sw, PARAM_LEFT_SYMBOL, PARAM_RIGHT_SYMBOL, resourceLoader, globalVars);
        return sw.toString();
    }

    protected void findProgram(CharBuffer contentBuffer) {
        int start = contentBuffer.position();
        int i1 = content.indexOf(PROGRAM_LEFT_SYMBOL, start);
        boolean flag = false;
        if (i1 != -1) {
            //findProgram
            int i2 = content.indexOf(PROGRAM_RIGHT_SYMBOL, start);
            if (i2 != -1) {
                flag = true;
                //匹配程序，开始解析
                //开始获取标记符前半段内容
                char[] str = new char[i1 - start];
                if (str.length > 0) {
                    contentBuffer.get(str, 0, i1 - start);
                    findString(CharBuffer.wrap(str));
                }
                //开始获取变量
                char[] v = new char[i2 - (start + str.length + PROGRAM_LEFT_SYMBOL.length())];
                contentBuffer.position(i1 + PROGRAM_LEFT_SYMBOL.length());
                contentBuffer.get(v, 0, v.length);
                contentBuilder.append(String.valueOf(v));
                contentBuffer.position(contentBuffer.position() + PROGRAM_RIGHT_SYMBOL.length());
                findProgram(contentBuffer);
            }
        }
        if (!flag) {
            if (contentBuffer.length() > 0) {
                findString(contentBuffer);
            }
        }
    }

    int pi = 0;

    public void findString(CharBuffer contentBuffer) {
        contentBuilder.append("printf(").append(contentBuffer.toString()).append(");");
    }
}
