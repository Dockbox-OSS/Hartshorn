package com.darwinreforged.server.core.entities.math;

public class AbstractVector3<N extends Number> extends AbstractVector2<N> {
    N y;

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

    public N getY() {
        return y;
    }

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
