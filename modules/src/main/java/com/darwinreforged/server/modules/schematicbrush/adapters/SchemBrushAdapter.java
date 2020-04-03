package com.darwinreforged.server.modules.schematicbrush.adapters;

import com.sk89q.worldedit.entity.Player;

import java.util.Optional;

public interface SchemBrushAdapter {

    boolean isPresent();

    Optional<Player> wrapPlayer(org.spongepowered.api.entity.living.player.Player player);
}
