package com.opdar.framework.utils.yeson;

import com.opdar.framework.aop.base.TypeReference;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.yeson.annotations.JSONField;
import com.opdar.framework.utils.yeson.convert.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Timestamp;
import java.util.*;

public class YesonParser {
    // {
    private int LEFT_CURLY_BRACE = 123;
    // }
    private int RIGHT_CURLY_BRACE = 125;
    // [
    private int LEFT_SQUARE_BRACE = 91;
    // ]
    private int RIGHT_SQUARE_BRACE = 93;
    // :
    private int COLON = 58;
    // ,
    private int COMMA = 44;
    // "
    private int QUOTATION = 34;
    private int BACKSLASH = 92;
    private int index;
    char[] jsonBuffer;

    boolean isParseArray = false;
    boolean isInit = true;

    private HashMap<Class, JSONConvert> converts = new HashMap<Class, JSONConvert>();

    public static void main(String[] args) {
        YesonParser parser = new YesonParser();
        Test2 test2 = parser.parseObject("{\"t3\":{\"a\":\"a\"},\"t4\":{\"time\":123456}}",new TypeReference<Test2<Test,STAT>>(){}._type);
        System.out.println(test2);
    }

    public String toJSONString(Object o) {
        if (o instanceof String) return (String) o;
        if (o instanceof Boolean) return String.valueOf(o);
        if (o instanceof Number) return String.valueOf(o);

        Class clz = o.getClass();
        if (o instanceof SeedExcuteItrf) {
            clz = clz.getSuperclass();
        }
        StringBuilder builder = new StringBuilder();
        if (o instanceof Collection) {
            toJSONStringWithArray(builder, (Collection) o);
        } else {
            if (o instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) o;
                if (map == null || map.size() == 0) {
                    return "null";
                }
                for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    Object fieldResult = map.get(key);
                    Class<?> fieldType = Void.class;
                    if (fieldResult != null) {
                        fieldType = fieldResult.getClass();
                    }
                    exeObjectResult(builder, key, fieldResult, fieldType);
                }
            } else {
                Field[] fields = clz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);

                    try {
                        String key = field.getName();
                        JSONField jsonField = field.getAnnotation(JSONField.class);
                        if(jsonField != null){
                            key = jsonField.value();
                            if(!jsonField.serializable())continue;
                        }
                        Object fieldResult = field.get(o);
                        Class<?> fieldType = Void.class;
                        if (fieldResult != null) {
                            fieldType = fieldResult.getClass();
                        }
                        exeObjectResult(builder, key, fieldResult, fieldType);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (builder.length() > 0) {
                builder.delete(builder.length() - 1, builder.length());
                builder.insert(0, "{");
                builder.append("}");
            } else {
                builder.append("null");
            }
        }
        return builder.toString();
    }

    private void exeObjectResult(StringBuilder builder, String key, Object fieldResult, Class<?> fieldType) {
        builder.append("\"").append(key).append("\":");
        if (Collection.class.isAssignableFrom(fieldType)) {
            Collection collection = (Collection) fieldResult;
            toJSONStringWithArray(builder, collection);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            builder.append(toJSONString(fieldResult));
        } else if (converts.containsKey(fieldType)) {
            JSONConvert convert = converts.get(fieldType);
            Object result = convert.convert(fieldResult);
            builder.append(result);
        } else {
            builder.append("null");
        }
        builder.append(",");
    }

    private void toJSONStringWithArray(StringBuilder builder, Collection collection) {
        if (collection != null) {
            builder.append("[");
            for (Iterator it = collection.iterator(); it.hasNext(); ) {
                builder.append(toJSONString(it.next()));
                if (it.hasNext())
                    builder.append(",");
            }
            builder.append("]");
        } else {
            builder.append("null");
        }
    }

    public YesonParser() {
        converts.put(String.class, new StringConvert());
        converts.put(int.class, new IntegerConvert());
        converts.put(short.class, new IntegerConvert());
        converts.put(long.class, new LongConvert());
        converts.put(char.class, new CharConvert());
        converts.put(float.class, new FloatConvert());
        converts.put(double.class, new DoubleConvert());
        converts.put(byte.class, new ByteConvert());
        converts.put(Integer.class, new IntegerConvert());
        converts.put(Short.class, new IntegerConvert());
        converts.put(Long.class, new LongConvert());
        converts.put(Character.class, new CharConvert());
        converts.put(Float.class, new FloatConvert());
        converts.put(Double.class, new DoubleConvert());
        converts.put(Byte.class, new ByteConvert());
        converts.put(Number.class, new NumberConvert());
        converts.put(Timestamp.class, new TimestapConvert());
        converts.put(Date.class, new DateConvert());
    }

    public void addConvert(Class type, JSONConvert convert) {
        converts.put(type, convert);
    }

    public JSONObject parse(String json) {
        jsonBuffer = json.toCharArray();
        index = 0;
        isInit = true;
        isParseArray = false;
        return toJSONObject();
    }

    public JSONArray parseArray(String json) {
        jsonBuffer = json.toCharArray();
        index = 0;
        isInit = true;
        isParseArray = false;
        return toJSONArray();
    }

    public <T> T parseType(String s, TypeReference<T> typeReference) {
        Type type = typeReference._type;
        return parseObject(s, type);
    }

    public <T> T parseObject(String json, java.lang.reflect.Type classType) {
        try {
            JSONObject object = parse(json);
            return object.getObject(classType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> List<T> parseArray(String s, Type classType) {
        JSONArray array = parseArray(s);
        return array.getArray(classType);
    }

    private JSONArray toJSONArray() {
        isParseArray = true;
        JSONArray array = new JSONArray();
        for (int i = index; i < jsonBuffer.length; i++) {
            index++;
            char ch = jsonBuffer[i];
            if (ch == LEFT_CURLY_BRACE) {
                array.add(toJSONObject());
                i = --index;
                continue;
            }
            if (ch == RIGHT_SQUARE_BRACE) {
                break;
            }
        }
        return array;
    }

    StringBuilder buff = new StringBuilder(1024);

    private JSONObject toJSONObject() {
        isParseArray = false;
        JSONObject root = new JSONObject();
        String key = null;
        Object value = null;

        /**
         * 1.parsing 2.stop parse
         */

        boolean isKey = true;

        try {
            for (int i = index; i < jsonBuffer.length; i++) {
                index++;
                char ch = jsonBuffer[i];
                if (root.type == 2 && ch < 33)
                    continue;

                if (ch == LEFT_CURLY_BRACE) {
                    if (isInit) {
                        isInit = false;
                        continue;
                    }
                    if (root.type == 2 && !isKey) {
                        value = toJSONObject();
                        root.put(key, value);
                        i = index;
                        isKey = true;
                    }
                    continue;
                }

                if (ch == RIGHT_SQUARE_BRACE) {
                    break;
                }
                if (ch == QUOTATION) {
                    if (root.type == 2 && isKey) {
                        root.type = 1;
                    } else if (root.type == 1 && isKey) {
                        root.type = 2;
                        key = buff.toString();
                        buff.delete(0, buff.length());
                        isKey = false;
                    } else if (root.type == 2 && !isKey) {
                        root.type = 1;
                    } else if (root.type == 1 && !isKey) {
                        root.type = 2;
                        value = buff.toString();
                        buff.delete(0, buff.length());
                        root.put(key, value);
                        isKey = true;
                    }
                    continue;
                }

                if (root.type != 1 && ch == RIGHT_CURLY_BRACE) {
                    if (root.lastchar == COLON && root.type == 2 && !isKey) {
                        value = buff.toString();
                        buff.delete(0, buff.length());
                        if (value.equals("null")) value = null;
                        root.put(key, value);
                        isKey = true;
                        root.lastchar = RIGHT_CURLY_BRACE;
                    }
                    return root;
                }
                if (root.type != 1 && ch == LEFT_SQUARE_BRACE) {
                    value = toJSONArray();
                    if (value.equals("null")) value = null;
                    root.put(key, value);
                    i = index;
                    isKey = true;
                    continue;
                }
                // if(ch == RIGHT_SQUARE_BRACE){
                // continue;
                // }
                if (root.type != 1 && ch == COLON) {
                    root.lastchar = COLON;
                    continue;
                }
                if (root.type != 1 && ch == COMMA) {
                    if (root.lastchar == COLON && root.type == 2 && !isKey) {
                        value = buff.toString();
                        buff.delete(0, buff.length());
                        if (value.equals("null")) value = null;
                        root.put(key, value);
                        isKey = true;
                        root.lastchar = COMMA;
                    }
                    continue;
                }
                buff.append(ch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // throw new JSONParserException(sb.charAt(sb.length()));
        }
        return root;
    }
}
