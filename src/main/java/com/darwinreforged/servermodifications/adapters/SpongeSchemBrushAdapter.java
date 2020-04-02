package com.darwinreforged.servermodifications.adapters;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

import java.util.Optional;

public class SpongeSchemBrushAdapter
        implements SchemBrushAdapter {

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public Optional<Player> wrapPlayer(org.spongepowered.api.entity.living.player.Player player) {
        return Optional.of(SpongeWorldEdit.inst().wrapPlayer(player));
    }
}
