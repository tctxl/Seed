package com.opdar.framework.template.base;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Expression {
    private String name;
    private String condition;
    private Part program;

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
        return program;
    }

    public void setProgram(String program) {
        ParserDefined defined = new ParserDefined();
        this.program = defined.parse(program);
    }

    @Override
    public String toString() {
        return "Expression{" +
                name +"=" + program +
                '}';
    }
}
