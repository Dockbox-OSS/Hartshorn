package com.darwinreforged.server.mcp.reference;

import com.darwinreforged.server.mcp.entities.Entities.Player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.UUID;

public class ForgeWorld {

    Reference<World> worldReference;

    public ForgeWorld(Reference<World> worldReference) {
        this.worldReference = worldReference;
    }

    public static ForgeWorld getFromPlayer(UUID uuid) {
        EntityPlayerMP playerMP = Player.getPlayer(uuid);
        if (playerMP == null) return null;
        else return new ForgeWorld(new Reference<>(playerMP.world));
    }

    public World getWorld() {
        if (this.worldReference == null) return null;
        return this.worldReference.getT();
    }

}
