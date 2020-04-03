package com.darwinreforged.server.mcp.reference;

import com.darwinreforged.server.mcp.registry.Player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.UUID;

public class ForgeWorld {

    ForgeReference<World> worldForgeReference;

    public ForgeWorld(ForgeReference<World> worldForgeReference) {
        this.worldForgeReference = worldForgeReference;
    }

    public static ForgeWorld getFromPlayer(UUID uuid) {
        EntityPlayerMP playerMP = Player.getPlayer(uuid);
        if (playerMP == null) return null;
        else return new ForgeWorld(new ForgeReference<>(playerMP.world));
    }

    public World getWorld() {
        if (this.worldForgeReference == null) return null;
        return this.worldForgeReference.getT();
    }


}
