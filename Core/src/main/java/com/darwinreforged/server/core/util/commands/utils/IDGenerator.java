package com.darwinreforged.server.core.util.commands.utils;

import java.util.HashMap;
import java.util.Map;

public class IDGenerator {

    private final Map<Class<?>, Integer> map = new HashMap<>();

    public String getId(Class<?> c) {
        c = ClassUtils.wrapPrimitive(c);
        int count = map.getOrDefault(c, 0);
        map.put(c, count + 1);
        return c.getSimpleName() + "#" + count;
    }
}
