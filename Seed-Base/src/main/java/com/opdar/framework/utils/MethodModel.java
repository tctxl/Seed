package com.opdar.framework.utils;

import com.opdar.framework.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class MethodModel {
	private int METHOD_TYPE = 0;
	private String methodName;
	private LinkedList<String> params = new LinkedList<String>();
	private ArrayList<Type> anotations = new ArrayList<Type>();
	private Type[] args;
    //paramName Type
    private LinkedHashMap<String,Type> paramAnotations = new LinkedHashMap<String,Type>();

    public boolean containsParamAnotation(String paramName){
        return paramAnotations.containsKey(paramName);
    }

    public Type getParamAnotation(String paramName){
        return paramAnotations.get(paramName);
    }
    public Type[] getArgs() {
        return args;
    }

    public ArrayList<Type> getAnotations() {
        return anotations;
    }

    public void setAnotations(ArrayList<Type> anotations) {
        this.anotations = anotations;
    }

    @Override
	public String toString() {
		return "MethodModel [METHOD_TYPE=" + METHOD_TYPE + ", methodName="
				+ methodName + ", params=" + params + ", anotations="
				+ anotations + ", args=" + Arrays.toString(args) + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return methodName.equals(obj);
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setStaticMethod() {
		METHOD_TYPE = 1;
	}

	public boolean isStaticMethod() {
		return METHOD_TYPE == 1;
	}

	public LinkedList<String> getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params.add(params);
	}
	
	public void setAnotation(Type anotation) {
		this.anotations.add(anotation);
	}

    public void addParamAnoatations(String paramName,Type type){
        paramAnotations.put(paramName, type);
    }

    public void removeParamAnoatations(Type type){
        paramAnotations.remove(type);
    }
    public boolean containsAnotation(Class clz){
        for(Type type : anotations){
            if(clz.getName().equals(type.getClassName())){
                return true;
            }
        }
        return false;
    }

	public void setArgs(Type[] args) {
		// TODO Auto-generated method stub
		this.args = args;
	}
}
