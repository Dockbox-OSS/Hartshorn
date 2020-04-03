package com.darwinreforged.server.modules.schematicbrush.adapters;

import com.sk89q.worldedit.entity.Player;

import java.util.Optional;

public class DummySchemBrushAdapter
        implements SchemBrushAdapter {
    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Optional<Player> wrapPlayer(org.spongepowered.api.entity.living.player.Player player) {
        return Optional.empty();
    }
}
