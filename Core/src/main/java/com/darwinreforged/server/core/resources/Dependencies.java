package com.darwinreforged.server.core.resources;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.WorldEdit;

public enum Dependencies implements Dependency {
    FASTASYNCWORLDEDIT(Dependency.of(Fawe.class.toGenericString()), true),
    NATIVE_WORLDEDIT(Dependency.of(WorldEdit.class.toGenericString()), true),
    PLOTSQUARED(Dependency.of("com.intellectualcrafters.plot.PS"), true),
    MULTICHAT(Dependency.of("xyz.olivermartin.multichat.local.common.MultiChatLocal"), true),
    VOXELSNIPER(Dependency.of("com.thevoxelbox.voxelsniper.VoxelSniper"), true),
    PLACEHOLDER_API(Dependency.of("me.clip.placeholderapi.PlaceholderAPI"), false);

    private final String mainClass;
    private final boolean harsh;

    Dependencies(Dependency dependency, boolean harsh) {
        this.mainClass = dependency.getMainClass();
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
