package com.darwinreforged.server.mcp.reference;

public class ForgeReference<T> {

    private final T t;

    public ForgeReference(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }
}
