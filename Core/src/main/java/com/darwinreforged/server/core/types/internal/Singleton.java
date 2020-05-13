package com.darwinreforged.server.core.types.internal;

/**
 The type Singleton.
 */
public abstract class Singleton {

    /**
     The constant instance.
     */
    protected static Singleton instance;

    /**
     Instantiates a new Singleton.

     @throws InstantiationException
     the instantiation exception
     */
    public Singleton() throws InstantiationException {
        if (instance != null) throw new InstantiationException("Singleton instance already exists");
        instance = this;
    }

}
