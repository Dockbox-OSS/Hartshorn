package com.darwinreforged.server.core.resources;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.WorldEdit;

public enum Dependencies implements Dependency {
    // TODO? Convert to getclass of types?
    FASTASYNCWORLDEDIT(Fawe.class.toGenericString(), true),
    NATIVE_WORLDEDIT(WorldEdit.class.toGenericString(), true),
    PLOTSQUARED("com.intellectualcrafters.plot.PS", true),
    MULTICHAT("xyz.olivermartin.multichat.local.common.MultiChatLocal", true),
    VOXELSNIPER("com.thevoxelbox.voxelsniper.VoxelSniper", true),
    PLACEHOLDER_API("me.clip.placeholderapi.PlaceholderAPI", false);

    private final String mainClass;
    private final boolean harsh;

    Dependencies(String mainClass, boolean harsh) {
        this.mainClass = mainClass;
        this.harsh = harsh;
    }

    @Override
    public String getMainClass() {
        return this.mainClass;
    }

    public boolean isLoaded() {
        if (!harsh) return true;

        try {
            Class.forName(this.mainClass);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
