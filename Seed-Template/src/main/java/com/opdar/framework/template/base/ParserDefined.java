package com.opdar.framework.template.base;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 俊帆 on 2015/8/3.
 */
public class ParserDefined {
    char END = ';';
    char SPACE = ' ';
    char NEWLINE = '\n';
    char ENTER = '\r';
    boolean isExpressionOpen = false;
    boolean isProgramOpen = false;
    boolean isFuncParse = false;
    int PROGRAM_STAT = 0;
    int skipBracket = 0;
    int skipFuncBracket = 0;
    int skipBrace = 0;
    Set<String> funcs = new HashSet<String>();

    public ParserDefined(){
        funcs.add("var ");
        funcs.add("printf");
    }

    public Part parse(String content) {
        Part part = new Part();
        CharBuffer charBuffer = CharBuffer.wrap(content);
        StringBuilder expression = new StringBuilder();
        StringBuilder condition = new StringBuilder();
        StringBuilder program = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        while (true) {
            if (charBuffer.position() == content.length()) break;
            char c = charBuffer.get();
            if (PROGRAM_STAT == 0 && c == SPACE) continue;
            if (PROGRAM_STAT == 0 && c == END)continue;
            if (PROGRAM_STAT == 0 && c == ENTER)continue;
            if (PROGRAM_STAT == 0 && c == NEWLINE)continue;

            if (isExpressionOpen || isProgramOpen) {
                temp.delete(0, temp.length());
            } else {
                if(!isFuncParse && funcs.contains(temp.toString().trim())){
                    isFuncParse = true;
                }
                if (skipFuncBracket == 0 && c == END && temp.length() > 0) {
                    expression.delete(0, expression.length());
                    String tt = temp.toString().trim();
                    if (tt.indexOf("var ") == 0) {
                        part.addPart(new Variable(tt));
                        temp.delete(0, temp.length());
                        if (PROGRAM_STAT == 1) PROGRAM_STAT = 0;
                        isFuncParse = false;
                    } else if(tt.indexOf("printf") == 0){
                        part.addPart(new Printf(tt));
                        temp.delete(0, temp.length());
                        if (PROGRAM_STAT == 1) PROGRAM_STAT = 0;
                        isFuncParse = false;
                    }else{
                        System.out.println("paser error:" + temp);
                    }
                    continue;
                }
                temp.append(c);
            }
            if(isFuncParse && c =='('){
                skipFuncBracket++;
                continue;
            }else if(isFuncParse && c == ')'){
                skipFuncBracket--;
                continue;
            }

            if (!isFuncParse && skipBracket == 0 && isExpressionOpen && c == ')') {
                isExpressionOpen = false;
                continue;
            }else if(!isFuncParse && isExpressionOpen && c == ')'){
                skipBracket--;
                continue;
            }
            if (!isFuncParse && PROGRAM_STAT == 1 && c == '(') {
                isExpressionOpen = true;
                PROGRAM_STAT = 2;
                continue;
            }else if(isFuncParse && isExpressionOpen && c == '(')skipBracket++;

            if (!isFuncParse && skipBrace == 0 && isProgramOpen && c == '}') {
                isProgramOpen = false;
                if (PROGRAM_STAT == 3){
                    PROGRAM_STAT = 0;
                    Expression expression1 = new Expression();
                    expression1.setName(expression.toString());
                    expression1.setCondition(condition.toString());
                    expression1.setProgram(program.toString());
                    part.addPart(expression1);
                    expression.delete(0, expression.length());
                    condition.delete(0, condition.length());
                    program.delete(0, program.length());
                }
                else new Throwable("Error");
                continue;
            }else if(!isFuncParse && !isExpressionOpen && isProgramOpen && c == '}'){
                skipBrace--;
            }
            if (!isFuncParse && PROGRAM_STAT == 2 && c == '{') {
                isProgramOpen = true;
                PROGRAM_STAT = 3;
                continue;
            }else if(!isFuncParse && PROGRAM_STAT == 3 && c == '{'){
                skipBrace++;
            }
            if (!isFuncParse && isProgramOpen) {
                program.append(c);
            } else if (!isFuncParse && isExpressionOpen) {
                condition.append(c);
            } else {
                PROGRAM_STAT = 1;
                expression.append(c);
            }
        }
        if (!isFuncParse && PROGRAM_STAT != 0) {
            System.out.println("error [" + expression + "]");
        }
        return part;
    }

}