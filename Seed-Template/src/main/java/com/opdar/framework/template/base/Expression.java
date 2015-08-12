package com.opdar.framework.template.base;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Expression {
    private String name;
    private String condition;
    private HashMap<Integer,Part> program = new HashMap<Integer, Part>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Part getProgram() {
        return getProgram(-1);
    }
    public Part getProgram(Integer i) {
        if(i == null)program.get(-1);
        return program.get(i);
    }

    public void setProgram(String program) {
        if(name.equals("switch")){
            StringBuilder builder = new StringBuilder();
            boolean caseStart = false;
            boolean caseProgramStart = false;
            HashSet<Integer> cases = new HashSet<Integer>();
            for(int i=0;i<program.length();i++){
                char c = program.charAt(i);
                if(c == '\r')continue;
                if(c == '\n')continue;
                if(c == ' ')continue;
                if(c == ':'){
                    cases.add(Integer.valueOf(builder.toString()));
                    caseStart = false;
                    builder.delete(0,builder.length());
                    caseProgramStart = true;
                    continue;
                }
                builder.append(c);
                if(builder.toString().equals("case")){
                    caseStart = true;
                    builder.delete(0,builder.length());
                    continue;
                }
                if(caseProgramStart){
                    if(builder.lastIndexOf("break;") >0){
                        builder.delete(builder.lastIndexOf("break;"),builder.length());
                        for(Integer j:cases){
                            setProgram(j,builder.toString());
                            cases.clear();
                            builder.delete(0,builder.length());
                        }
                    }
                }
            }
        }else{
            setProgram(-1,program);
        }
    }

    public void setProgram(int i,String program) {
        ParserDefined defined = new ParserDefined();
        this.program.put(i,defined.parse(program));
    }

    @Override
    public String toString() {
        return "Expression{" +
                name +"=" + program +
                '}';
    }
}
