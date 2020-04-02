package com.darwinreforged.servermodifications.adapters;

import com.sk89q.worldedit.entity.Player;

import java.util.Optional;

public interface SchemBrushAdapter {

    boolean isPresent();

    Optional<Player> wrapPlayer(org.spongepowered.api.entity.living.player.Player player);
}
