package com.darwinreforged.server.core.entities;

public class AbstractVector2<N extends Number> {

    N x;
    N z;

    public AbstractVector2(N x, N z) {
        this.x = x;
        this.z = z;
    }

    public N getX() {
        return x;
    }

    public void setX(N x) {
        this.x = x;
    }

    public N getZ() {
        return z;
    }

    public void setZ(N z) {
        this.z = z;
    }
}
