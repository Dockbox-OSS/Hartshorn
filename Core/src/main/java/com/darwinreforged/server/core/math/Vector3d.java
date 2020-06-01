package com.darwinreforged.server.core.math;

/**
 The type Vector 3 d.
 */
public class Vector3d extends AbstractVector3<Double> {

    /**
     Instantiates a new Vector 3 d.

     @param x
     the x
     @param y
     the y
     @param z
     the z
     */
    public Vector3d(Double x, Double y, Double z) {
        super(x, y, z);
    }

    @Override
    public String toString() {
        return "x=" + x + ", z=" + z + ", y=" + y;
    }
}
