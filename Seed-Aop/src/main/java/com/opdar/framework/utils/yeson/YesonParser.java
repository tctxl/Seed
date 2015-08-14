package com.opdar.framework.utils.yeson;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.utils.yeson.convert.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.BatchUpdateException;
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
        List<Test> o = parser.parseArray("[{\"a\":\"3321\"},{\"a\":\"222\"}]", Test.class);
        System.out.println(o);
    }

    public String toJSONString(Object o) {
        Class clz = o.getClass();
        if (o instanceof SeedExcuteItrf) {
            clz = clz.getSuperclass();
        }
        StringBuilder builder = new StringBuilder();
        if(o instanceof Collection){
            toJSONStringWithArray(builder, (Collection) o);
        }else{
            builder.append("{");
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    String key = field.getName();
                    builder.append("\"").append(key).append("\":");
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection collection = (Collection) field.get(o);
                        toJSONStringWithArray(builder,collection);
                    } else if (converts.containsKey(field.getType())) {
                        JSONConvert convert = converts.get(field.getType());
                        Object result = convert.convert(field.get(o));
                        builder.append(result);
                    }else{
                        builder.append("null");
                    }
                    builder.append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (builder.length() > 0) {
                builder.delete(builder.length() - 1, builder.length());
            }
            builder.append("}");
        }
        return builder.toString();
    }

    private void toJSONStringWithArray(StringBuilder builder,Collection collection){
        if(collection != null){
            builder.append("[");
            for(Iterator it = collection.iterator();it.hasNext();){
                builder.append(toJSONString(it.next()));
            }
            builder.append("]");
        }else{
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

    public void addConvert(Class type,JSONConvert convert){
        converts.put(type,convert);
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

    public <T> T parseObject(String json, Class<T> clz) {
        try {
            JSONObject object = parse(json);
            return object.getObject(clz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> List<T> parseArray(String s, Class<T> clz) {
        JSONArray array = parseArray(s);
        return array.getArray(clz);
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

                if (ch == RIGHT_CURLY_BRACE) {
                    if (root.lastchar == COLON && root.type == 2 && !isKey) {
                        value = buff.toString();
                        buff.delete(0, buff.length());
                        root.put(key, value);
                        isKey = true;
                        root.lastchar = RIGHT_CURLY_BRACE;
                    }
                    return root;
                }
                if (ch == LEFT_SQUARE_BRACE) {
                    value = toJSONArray();
                    root.put(key, value);
                    i = index;
                    isKey = true;
                    continue;
                }
                // if(ch == RIGHT_SQUARE_BRACE){
                // continue;
                // }
                if (ch == COLON) {
                    root.lastchar = COLON;
                    continue;
                }
                if (ch == COMMA) {
                    if (root.lastchar == COLON && root.type == 2 && !isKey) {
                        value = buff.toString();
                        buff.delete(0, buff.length());
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
