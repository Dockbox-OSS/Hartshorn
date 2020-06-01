package com.darwinreforged.server.core.types.location;

import com.darwinreforged.server.core.math.AbstractVector3;

/**
 The type Darwin location.
 */
public class DarwinLocation {

    private DarwinWorld world;
    private AbstractVector3<? extends Number> vectorLoc;

    /**
     Instantiates a new Darwin location.

     @param world
     the world
     @param vectorLoc
     the vector loc
     */
    public DarwinLocation(DarwinWorld world, AbstractVector3<? extends Number> vectorLoc) {
        this.world = world;
        this.vectorLoc = vectorLoc;
    }

    /**
     Gets world.

     @return the world
     */
    public DarwinWorld getWorld() {
        return world;
    }

    /**
     Sets world.

     @param world
     the world
     */
    public void setWorld(DarwinWorld world) {
        this.world = world;
    }

    /**
     Gets vector loc.

     @return the vector loc
     */
    public AbstractVector3<? extends Number> getVectorLoc() {
        return vectorLoc;
    }

    /**
     Sets vector loc.

     @param vectorLoc
     the vector loc
     */
    public void setVectorLoc(AbstractVector3<? extends Number> vectorLoc) {
        this.vectorLoc = vectorLoc;
    }

    /**
     Gets x.

     @return the x
     */
    public Number getX() {
        return vectorLoc.getX();
    }

    /**
     Gets y.

     @return the y
     */
    public Number getY() {
        return vectorLoc.getY();
    }

    /**
     Gets z.

     @return the z
     */
    public Number getZ() {
        return vectorLoc.getZ();
    }

    @Override
    public String toString() {
        return vectorLoc.toString() + "@" + world;
    }
}
