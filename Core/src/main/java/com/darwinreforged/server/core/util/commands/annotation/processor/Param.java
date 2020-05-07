package com.darwinreforged.server.core.util.commands.annotation.processor;

import com.darwinreforged.server.core.util.commands.annotation.Join;
import com.darwinreforged.server.core.util.commands.annotation.Src;
import com.darwinreforged.server.core.util.commands.command.Flags;
import com.darwinreforged.server.core.util.commands.utils.IDGenerator;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;


public class Param {

    private final String id;
    private final Type paramType;
    private final Class<?> type;

    public Param(String id, Class<?> clazz, Type type) {
        this.id = id;
        this.type = clazz;
        this.paramType = type;
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public Type getParamType() {
        return paramType;
    }

    public enum Type {
        NODE(0),
        SOURCE(0),
        ANY(1),
        ONE(1),
        JOIN(2),
        VARARG(3),
        FLAG(4),
        ;

        private final int priority;

        Type(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return priority;
        }
    }

    public static Param of(IDGenerator generator, Parameter parameter) {
        Type paramType = getParamType(parameter);
        Class<?> type = getActualType(parameter);
        String id = generator.getId(type);
        return new Param(id, type, paramType);
    }

    public static Type getParamType(Parameter parameter) {
        if (parameter.isAnnotationPresent(Join.class)) {
            return Type.JOIN;
        }
        else if (parameter.isAnnotationPresent(Src.class)) {
            return Type.SOURCE;
        }
        else if (parameter.getType() == Flags.class) {
            return Type.FLAG;
        }
        else if (parameter.getType() == Collection.class) {
            return Type.ANY;
        }
        if (parameter.isVarArgs() || parameter.getType().isArray()) {
            return Type.VARARG;
        }
        return Type.ONE;
    }

    private static Class<?> getActualType(Parameter parameter) {
        if (parameter.isVarArgs() || parameter.getType().isArray()) {
            return parameter.getType().getComponentType();
        }

        if (Collection.class == parameter.getType()) {
            ParameterizedType paramT = (ParameterizedType) parameter.getParameterizedType();
            return (Class<?>) paramT.getActualTypeArguments()[0];
        }

        return parameter.getType();
    }
}
