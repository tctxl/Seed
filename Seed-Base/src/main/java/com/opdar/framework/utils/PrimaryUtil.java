package com.opdar.framework.utils;

import com.opdar.framework.asm.Type;


/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class PrimaryUtil {

    public static boolean isPrimary(Class<?> clz) {
        if (
                Integer.class.isAssignableFrom(clz)
                || (Long.class.isAssignableFrom(clz))
                || (Double.class.isAssignableFrom(clz))
                || (Float.class.isAssignableFrom(clz))
                || (Character.class.isAssignableFrom(clz))
                || (Byte.class.isAssignableFrom(clz))
                || (Boolean.class.isAssignableFrom(clz))
                || (Short.class.isAssignableFrom(clz))
                || (String.class.isAssignableFrom(clz))
                || clz.isPrimitive()
                ) {
            return true;
        }
        return false;
    }


    public static <T> T cast(Object value, Class<?> clz) {
        if (Number.class.isAssignableFrom(clz)) {
            Object number = null;
            if (Integer.class.isAssignableFrom(clz) || int.class.isAssignableFrom(clz)) {
                number = cast(value, Type.INT);
            } else if (Long.class.isAssignableFrom(clz) || long.class.isAssignableFrom(clz)) {
                number = cast(value, Type.LONG);
            } else if (Double.class.isAssignableFrom(clz) || double.class.isAssignableFrom(clz)) {
                number = cast(value, Type.DOUBLE);
            } else if (Float.class.isAssignableFrom(clz) || float.class.isAssignableFrom(clz)) {
                number = cast(value, Type.FLOAT);
            } else if (Character.class.isAssignableFrom(clz) || char.class.isAssignableFrom(clz)) {
                number = cast(value, Type.CHAR);
            } else if (Byte.class.isAssignableFrom(clz) || byte.class.isAssignableFrom(clz)) {
                number = cast(value, Type.BYTE);
            } else if (Boolean.class.isAssignableFrom(clz) || boolean.class.isAssignableFrom(clz)) {
                number = cast(value, Type.BOOLEAN);
            } else if (Short.class.isAssignableFrom(clz) || short.class.isAssignableFrom(clz)) {
                number = cast(value, Type.SHORT);
            }
            return (T) number;
        }
        return (T) value;
    }

    public static <T> T cast(Object value, Type type) {
        switch (type.getSort()) {
            case Type.OBJECT:
                try {
                    Class clz = Thread.currentThread().getContextClassLoader().loadClass(type.getClassName());
                    if (Number.class.isAssignableFrom(clz)) {
                        Object number = null;
                        if (Integer.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.INT);
                        } else if (Long.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.LONG);
                        } else if (Double.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.DOUBLE);
                        } else if (Float.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.FLOAT);
                        } else if (Character.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.CHAR);
                        } else if (Byte.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.BYTE);
                        } else if (Boolean.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.BOOLEAN);
                        } else if (Short.class.isAssignableFrom(clz)) {
                            number = cast(value, Type.SHORT);
                        }
                        return (T) number;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return (T) value;
    }


    public static <T> T cast(Object value, int sort) {
        switch (sort) {
            case Type.BOOLEAN:
                if (value == null) {
                    value = false;
                } else {
                    value = Boolean.valueOf(value.toString());
                }
                break;
            case Type.CHAR:
                if (value == null) {
                    value = '0';
                } else {
                    value = value.toString().charAt(0);
                }
                break;
            case Type.BYTE:
                if (value == null) {
                    value = 0;
                } else {
                    value = Byte.valueOf(value.toString());
                }
                break;
            case Type.SHORT:
                if (value == null) {
                    value = 0;
                } else {
                    value = Short.valueOf(value.toString());
                }
                break;
            case Type.INT:
                if (value == null) {
                    value = 0;
                } else {
                    value = Integer.valueOf(value.toString());
                }
                break;
            case Type.FLOAT:
                if (value == null) {
                    value = 0f;
                } else {
                    value = Float.valueOf(value.toString());
                }
                break;
            case Type.LONG:
                if (value == null) {
                    value = 0l;
                } else {
                    value = Long.valueOf(value.toString());
                }
                break;
            case Type.DOUBLE:
                if (value == null) {
                    value = 0d;
                } else {
                    value = Double.valueOf(value.toString());
                }
                break;
        }
        return (T) value;
    }
}
