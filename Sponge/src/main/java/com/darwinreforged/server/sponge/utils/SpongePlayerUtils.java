package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.entities.living.inventory.DarwinItem;
import com.darwinreforged.server.core.entities.living.state.GameModes;
import com.darwinreforged.server.core.entities.location.DarwinLocation;
import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.entities.math.Vector3d;
import com.darwinreforged.server.core.init.DarwinServer;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityImplementation(PlayerUtils.class)
public class SpongePlayerUtils extends PlayerUtils {

    @Override
    public void broadcast(com.darwinreforged.server.core.entities.chat.Text message) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(message));
    }

    @Override
    public void broadcastIfPermitted(com.darwinreforged.server.core.entities.chat.Text message, String permission) {
        Sponge.getServer().getOnlinePlayers().parallelStream().filter(p -> p.hasPermission(permission)).forEach(p -> p.sendMessage(Text.of(message)));
    }

    @Override
    public void tell(DarwinPlayer player, com.darwinreforged.server.core.entities.chat.Text message) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(spp -> spp.sendMessage(Text.of(Translations.PREFIX.s(), message)));
    }

    @Override
    public void tellPlain(DarwinPlayer player, com.darwinreforged.server.core.entities.chat.Text message) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(spp -> spp.sendMessage(Text.of(message)));
    }

    @Override
    public boolean isOnline(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUniqueId()).map(User::isOnline).orElse(false);
    }

    @Override
    public void kick(DarwinPlayer player) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(Player::kick);
    }

    @Override
    public boolean hasPermission(DarwinPlayer player, String permission) {
        return Sponge.getServer().getPlayer(player.getUniqueId()).map(u -> u.hasPermission(permission)).orElse(false);
    }

    @Override
    public Optional<DarwinLocation> getLocation(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUniqueId()).map(p -> {
            Location<World> worldLocation = p.getLocation();
            DarwinWorld darwinWorld = new DarwinWorld(worldLocation.getExtent().getUniqueId(), worldLocation.getExtent().getName());
            Vector3d vector3d = new Vector3d(worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
            return Optional.of(new DarwinLocation(darwinWorld, vector3d));
        }).orElse(Optional.empty());
    }

    @Override
    public DarwinItem<?> getItemInHand(DarwinPlayer player, boolean primaryHand) {
        Optional<ItemStack> itemStack = Sponge.getServer().getPlayer(player.getUniqueId())
                .flatMap(spp -> spp.getItemInHand(primaryHand ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND));
        return itemStack.map(DarwinItem::new).orElse(null);
    }

    @Override
    public GameModes getGameMode(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUniqueId()).flatMap(sp -> sp.getGameModeData().get(Keys.GAME_MODE)).map(gm -> {
            try {
                return Enum.valueOf(GameModes.class, gm.getName().toUpperCase());
            } catch (NullPointerException | IllegalArgumentException e) {
                return GameModes.UNKNOWN;
            }
        }).orElse(GameModes.UNKNOWN);
    }

    @Override
    public void setGameMode(GameModes mode, DarwinPlayer player) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(sp -> {
            org.spongepowered.api.entity.living.player.gamemode.GameMode spMode;
            switch (mode) {
                case CREATIVE:
                case UNKNOWN:
                default:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.CREATIVE;
                    break;
                case SURVIVAL:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.SURVIVAL;
                    break;
                case SPECTATOR:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.SPECTATOR;
                    break;
                case ADVENTURE:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.ADVENTURE;
                    break;
            }
            sp.getGameModeData().set(Keys.GAME_MODE, spMode);
        });
    }

    @Override
    public void executeCmd(String cmd, Target target) {
        if (target instanceof DarwinPlayer || target instanceof DarwinServer) {
            if (isConsole(target))
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
            else
                Sponge.getServer().getPlayer(target.getUniqueId()).ifPresent(sp -> Sponge.getCommandManager().process(sp, cmd));
        } else {
            System.err.printf("Tried executing '%s' as non-player source (%s)%n", cmd, target.getClass());
        }
    }

    @Override
    public List<DarwinPlayer> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers().stream().map(sp -> new DarwinPlayer(sp.getUniqueId(), sp.getName())).collect(Collectors.toList());
    }
}
