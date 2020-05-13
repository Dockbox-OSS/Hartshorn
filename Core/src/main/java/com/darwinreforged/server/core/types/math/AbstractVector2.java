package com.darwinreforged.server.core.types.math;

/**
 The type Abstract vector 2.

 @param <N>
 the type parameter
 */
public class AbstractVector2<N extends Number> {

    /**
     The X.
     */
    N x;
    /**
     The Z.
     */
    N z;

    /**
     Instantiates a new Abstract vector 2.

     @param x
     the x
     @param z
     the z
     */
    public AbstractVector2(N x, N z) {
        this.x = x;
        this.z = z;
    }

    /**
     Gets x.

     @return the x
     */
    public N getX() {
        return x;
    }

    /**
     Sets x.

     @param x
     the x
     */
    public void setX(N x) {
        this.x = x;
    }

    /**
     Gets z.

     @return the z
     */
    public N getZ() {
        return z;
    }

    /**
     Sets z.

     @param z
     the z
     */
    public void setZ(N z) {
        this.z = z;
    }
}
