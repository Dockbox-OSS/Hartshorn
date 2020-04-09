package com.darwinreforged.server.core.entities;

public class DarwinLocation {

    private DarwinWorld world;
    private AbstractVector3<? extends Number> vectorLoc;

    public DarwinLocation(DarwinWorld world, AbstractVector3<? extends Number> vectorLoc) {
        this.world = world;
        this.vectorLoc = vectorLoc;
    }

    public DarwinWorld getWorld() {
        return world;
    }

    public void setWorld(DarwinWorld world) {
        this.world = world;
    }

    public AbstractVector3<? extends Number> getVectorLoc() {
        return vectorLoc;
    }

    public void setVectorLoc(AbstractVector3<? extends Number> vectorLoc) {
        this.vectorLoc = vectorLoc;
    }

    public Number getX() {
        return vectorLoc.getX();
    }

    public Number getY() {
        return vectorLoc.getY();
    }

    public Number getZ() {
        return vectorLoc.getZ();
    }
}
