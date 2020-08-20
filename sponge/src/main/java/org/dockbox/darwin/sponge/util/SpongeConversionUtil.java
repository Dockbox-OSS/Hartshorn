package org.dockbox.darwin.sponge.util;

import com.flowpowered.math.vector.Vector3d;

import org.dockbox.darwin.core.objects.tuple.Vector3D;
import org.dockbox.darwin.core.objects.user.Gamemode;
import org.dockbox.darwin.sponge.objects.location.SpongeLocation;
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
        World world = toSponge(location.getWorld());
        Vector3d vector3d = new Vector3d(location.getX().doubleValue(), location.getY().doubleValue(), location.getZ().doubleValue());
        return new Location<>(world, vector3d);
    }

    @NotNull
    public static org.dockbox.darwin.core.objects.location.Location fromSponge(Location<World> location) {
        org.dockbox.darwin.core.objects.location.World world = fromSponge(location.getExtent());
        Vector3D vector3D = new Vector3D(location.getX(), location.getY(), location.getZ());
        return new SpongeLocation(vector3D, world);
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
