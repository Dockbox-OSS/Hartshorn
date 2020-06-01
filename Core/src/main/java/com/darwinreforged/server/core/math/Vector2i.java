package com.darwinreforged.server.core.math;

/**
 The type Vector 2 i.
 */
public class Vector2i extends AbstractVector2<Integer> {
    /**
     Instantiates a new Vector 2 i.

     @param x
     the x
     @param z
     the z
     */
    public Vector2i(Integer x, Integer z) {
        super(x, z);
    }

    @Override
    public String toString() {
        return "x=" + x + ", z=" + z;
    }
}
