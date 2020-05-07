package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.living.inventory.DarwinItem;
import com.darwinreforged.server.core.entities.living.state.GameModes;
import com.darwinreforged.server.core.entities.location.DarwinLocation;
import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.entities.math.Vector3d;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.PlayerUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@UtilityImplementation(PlayerUtils.class)
public class SpongePlayerUtils extends PlayerUtils {

    @Override
    public void broadcast(String message) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(message));
    }

    @Override
    public void broadcastIfPermitted(String message, String permission) {
        Sponge.getServer().getOnlinePlayers().parallelStream().filter(p -> p.hasPermission(permission)).forEach(p -> p.sendMessage(Text.of(message)));
    }

    @Override
    public void tell(DarwinPlayer player, String message) {
        Sponge.getServer().getPlayer(player.getUuid()).ifPresent(spp -> spp.sendMessage(Text.of(Translations.PREFIX.s(), message)));
    }

    @Override
    public void tellPlain(DarwinPlayer player, String message) {
        Sponge.getServer().getPlayer(player.getUuid()).ifPresent(spp -> spp.sendMessage(Text.of(message)));
    }

    @Override
    public boolean isOnline(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUuid()).map(User::isOnline).orElse(false);
    }

    @Override
    public void kick(DarwinPlayer player) {
        Sponge.getServer().getPlayer(player.getUuid()).ifPresent(Player::kick);
    }

    @Override
    public boolean hasPermission(DarwinPlayer player, String permission) {
        return Sponge.getServer().getPlayer(player.getUuid()).map(u -> u.hasPermission(permission)).orElse(false);
    }

    @Override
    public Optional<DarwinLocation> getLocation(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUuid()).map(p -> {
            Location<World> worldLocation = p.getLocation();
            DarwinWorld darwinWorld = new DarwinWorld(worldLocation.getExtent().getUniqueId(), worldLocation.getExtent().getName());
            Vector3d vector3d = new Vector3d(worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
            return Optional.of(new DarwinLocation(darwinWorld, vector3d));
        }).orElse(Optional.empty());
    }

    @Override
    public DarwinItem<?> getItemInHand(DarwinPlayer player, boolean primaryHand) {
        Optional<ItemStack> itemStack = Sponge.getServer().getPlayer(player.getUuid())
                .flatMap(spp -> spp.getItemInHand(primaryHand ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND));
        return itemStack.map(DarwinItem::new).orElse(null);
    }
}
