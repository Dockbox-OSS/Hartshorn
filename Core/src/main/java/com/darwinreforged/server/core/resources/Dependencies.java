package com.darwinreforged.server.core.resources;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.WorldEdit;

public enum Dependencies implements Dependency {
    // TODO? Convert to getclass of types?
    FASTASYNCWORLDEDIT(Fawe.class.toGenericString()),
    NATIVE_WORLDEDIT(WorldEdit.class.toGenericString()),
    PLOTSQUARED("com.intellectualcrafters.plot.PS"),
    MULTICHAT("xyz.olivermartin.multichat.local.common.MultiChatLocal"),
    VOXELSNIPER("com.thevoxelbox.voxelsniper.VoxelSniper");

    private final String mainClass;

    Dependencies(String mainClass) {
        this.mainClass = mainClass;
    }

    @Override
    public String getMainClass() {
        return this.mainClass;
    }

    public boolean isLoaded() {
        try {
            Class.forName(this.mainClass);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
