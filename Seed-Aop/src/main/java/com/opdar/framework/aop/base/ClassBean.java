package com.opdar.framework.aop.base;

import com.opdar.framework.asm.Type;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/8.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ClassBean {

    private int version;
    private Class<?> seedClz;
    private List<AnotationInfo> anotations = new LinkedList<AnotationInfo>();

    public void setAnotation(AnotationInfo anotation) {
        this.anotations.add(anotation);
    }

    public List<AnotationInfo> getAnotations() {
        return anotations;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setSeedClz(Class<?> seedClz) {
        this.seedClz = seedClz;
    }

    public Class<?> getSeedClz() {
        return seedClz;
    }

    public static class AnotationInfo{
        private String desc;
        private Type type;
        private List<AnotationValue> value = new LinkedList<AnotationValue>();

        public static class AnotationValue{
            private String name;
            private Object value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getValue() {
                return value;
            }

            public void setValue(Object value) {
                this.value = value;
            }
        }

        public List<AnotationValue> getValue() {
            return value;
        }

        public void setValue(AnotationValue value) {
            this.value.add(value);
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

    }

    public static class FieldInfo{
        private String desc;
        private Type type;
        private String name;
        private Object defaultValue;
        private int access;
        private List<AnotationInfo> anotations;
        private Field field;

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAccess(int access) {
            this.access = access;
        }

        public int getAccess() {
            return access;
        }

        public void setAnotation(AnotationInfo anotation) {
            if(anotations == null)anotations = new LinkedList<AnotationInfo>();
            this.anotations.add(anotation);
        }

        public List<AnotationInfo> getAnotations() {
            return anotations;
        }

        public void setField(Field field) {
            this.field = field;
            field.setAccessible(true);
        }

        public Field getField() {
            return field;
        }
    }
    public static class MethodInfo{
        private String desc;
        private Type type;
        private String name;
        private Type[] args;
        private int access;
        private List<AnotationInfo> anotations = new LinkedList<AnotationInfo>();
        private LinkedList<LocalVar> localVars = new LinkedList<LocalVar>();

        private HashMap<String,Integer> argsSort= new HashMap<String, Integer>();

        public HashMap<String, Integer> getArgsSort() {
            return argsSort;
        }

        public void setArgsSort(String key) {
            this.argsSort.put(key,argsSort.size());
        }

        public static class LocalVar{
            private String desc;
            private Type type;
            private String name;
            private int index;
            private LinkedList<String> signatureTypes = new LinkedList<String>();
            private LinkedList<AnotationInfo> annotations = new LinkedList<AnotationInfo>();
            private String signature;

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public Type getType() {
                return type;
            }

            public void setType(Type type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public int getIndex() {
                return index;
            }

            public void setSignatureTypes(LinkedList<String> signatureTypes) {
                this.signatureTypes = signatureTypes;
            }

            public LinkedList<String> getSignatureTypes() {
                return signatureTypes;
            }

            public void setAnnotations(AnotationInfo annotations) {
                this.annotations.add(annotations);
            }

            public LinkedList<AnotationInfo> getAnnotations() {
                return annotations;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getSignature() {
                return signature;
            }
        }

        public LinkedList<LocalVar> getLocalVars() {
            return localVars;
        }

        public void setLocalVars(LocalVar localVar) {
            this.localVars.add(localVar);
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setArgs(Type[] args) {
            this.args = args;
        }

        public Type[] getArgs() {
            return args;
        }

        public void setAccess(int access) {
            this.access = access;
        }

        public int getAccess() {
            return access;
        }

        public void setAnotation(AnotationInfo anotation) {
            this.anotations.add(anotation);
        }

        public List<AnotationInfo> getAnotations() {
            return anotations;
        }

    }
    private String className;
    private String typeName;
    private List<FieldInfo> field = new LinkedList<FieldInfo>();
    private Map<String,Integer> fieldSort = new HashMap<String, Integer>();
    private List<MethodInfo> methods = new LinkedList<MethodInfo>();
    private Map<String,Integer> methodSort = new HashMap<String, Integer>();

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(MethodInfo method) {
        this.methods.add(method);
        setMethodSort(method.getName(),methodSort.size());
    }

    public Map<String, Integer> getFieldSort() {
        return fieldSort;
    }

    public Map<String, Integer> getMethodSort() {
        return methodSort;
    }


    public void setFieldSort(String fieldName,Integer sort) {
        this.fieldSort.put(fieldName,sort);
    }

    public void setMethodSort(String fieldName,Integer sort) {
        this.methodSort.put(fieldName,sort);
    }

    public List<FieldInfo> getField() {
        return field;
    }

    public void setField(FieldInfo field) {
        this.field.add(field);
        setFieldSort(field.getName(),fieldSort.size());
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
