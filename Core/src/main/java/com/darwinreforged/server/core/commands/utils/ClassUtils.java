package com.darwinreforged.server.core.commands.utils;

import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

    private static final Map<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>() {{
        this.put(boolean.class, Boolean.class);
        this.put(byte.class, Byte.class);
        this.put(char.class, Character.class);
        this.put(double.class, Double.class);
        this.put(float.class, Float.class);
        this.put(int.class, Integer.class);
        this.put(long.class, Long.class);
        this.put(short.class, Short.class);
    }};

    public static Class<?> wrapPrimitive(Class<?> c) {
        return primitives.getOrDefault(c, c);
    }

    public static Object cast(Object in, Class<?> type) {
        if (Number.class.isAssignableFrom(type)) {
            Number number = (Number) in;
            if (type == float.class || type == Float.class) {
                return number.floatValue();
            }
            if (type == short.class || type == Short.class) {
                return number.shortValue();
            }
            if (type == byte.class || type == Byte.class) {
                return number.byteValue();
            }
        }
        return in;
    }
}
