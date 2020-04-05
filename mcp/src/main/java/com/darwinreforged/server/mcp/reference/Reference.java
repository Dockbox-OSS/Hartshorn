package com.darwinreforged.server.mcp.reference;

public class Reference<T> {

    private final T t;

    public Reference(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }
}
