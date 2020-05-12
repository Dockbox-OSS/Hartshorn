package com.darwinreforged.server.core.types.internal;

public abstract class Singleton {

    protected static Singleton instance;

    public Singleton() throws InstantiationException {
        if (instance != null) throw new InstantiationException("Singleton instance already exists");
        instance = this;
    }

}
