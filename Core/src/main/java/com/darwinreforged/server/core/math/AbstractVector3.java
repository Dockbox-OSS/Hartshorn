package com.darwinreforged.server.core.math;

/**
 The type Abstract vector 3.

 @param <N>
 the type parameter
 */
public class AbstractVector3<N extends Number> extends AbstractVector2<N> {
    /**
     The Y.
     */
    N y;

    /**
     Instantiates a new Abstract vector 3.

     @param x
     the x
     @param y
     the y
     @param z
     the z
     */
    public AbstractVector3(N x, N y, N z) {
        super(x, z);
        super.x = x;
        this.y = y;
        super.z = z;
    }

    public N getX() {
        return x;
    }

    public void setX(N x) {
        super.x = x;
    }

    /**
     Gets y.

     @return the y
     */
    public N getY() {
        return y;
    }

    /**
     Sets y.

     @param y
     the y
     */
    public void setY(N y) {
        this.y = y;
    }

    public N getZ() {
        return z;
    }

    public void setZ(N z) {
        super.z = z;
    }
}
