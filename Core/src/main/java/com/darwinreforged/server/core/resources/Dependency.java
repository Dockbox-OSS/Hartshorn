package com.darwinreforged.server.core.resources;

public interface Dependency {

    String getMainClass();

    static Dependency of(String className) {
        return () -> className;
    }

    static Dependency of(Class<?> clazz) {
        return () -> clazz.toGenericString();
    }

}
