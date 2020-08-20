package org.dockbox.darwin.sponge.util;

import org.dockbox.darwin.core.objects.user.Gamemode;
import org.dockbox.darwin.sponge.objects.location.SpongeWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SpongeConversionUtil {

    @NotNull
    public static Text toSponge(org.dockbox.darwin.core.text.Text message) {
        // TODO
        message.append("");
        return Text.EMPTY;
    }

    @NotNull
    public static org.dockbox.darwin.core.text.Text fromSponge(Text message) {
        // TODO
        return new org.dockbox.darwin.core.text.Text();
    }

    @NotNull
    public static Location<World> toSponge(org.dockbox.darwin.core.objects.location.Location location) {
        // TODO
        return null;
    }

    @NotNull
    public static org.dockbox.darwin.core.objects.location.Location fromSponge(Location<?> location) {
        // TODO
        return org.dockbox.darwin.core.objects.location.Location.Companion.getEMPTY();
    }

    @NotNull
    public static World toSponge(org.dockbox.darwin.core.objects.location.World world) {
        if (world instanceof SpongeWorld) return ((SpongeWorld) world).getReference();
        else
            return Sponge.getServer().getWorld(world.getWorldUniqueId())
                    .orElseThrow(() -> new RuntimeException("World reference not present on server"));
    }

    @NotNull
    public static org.dockbox.darwin.core.objects.location.World fromSponge(World world) {
        return new SpongeWorld(world.getUniqueId(), world.getName());
    }

    public static GameMode toSponge(Gamemode gamemode) {
        switch (gamemode) {
            case SURVIVAL:
                return GameModes.SURVIVAL;
            case CREATIVE:
                return GameModes.CREATIVE;
            case ADVENTURE:
                return GameModes.ADVENTURE;
            case SPECTATOR:
                return GameModes.SPECTATOR;
            case OTHER:
            default:
                return GameModes.NOT_SET;
        }
    }

    public static Gamemode fromSponge(GameMode gamemode) {
        try {
            return Enum.valueOf(Gamemode.class, gamemode.toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }
}
