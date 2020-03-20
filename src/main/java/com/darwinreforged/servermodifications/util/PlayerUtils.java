package com.darwinreforged.servermodifications.util;

import com.darwinreforged.servermodifications.translations.Translations;
import com.intellectualcrafters.plot.object.PlotPlayer;
import io.github.nucleuspowered.nucleus.modules.core.datamodules.CoreUserDataModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class PlayerUtils {

    /*
     Online status of players
     */
    public static boolean isUserOnline(UUID uuid) {
        return Sponge.getServer().getOnlinePlayers().stream().anyMatch(p -> p.getUniqueId().equals(uuid));
    }

    public static boolean isUserOnline(String name) {
        return Sponge.getServer().getOnlinePlayers().stream().anyMatch(p -> p.getName().equals(name));
    }

    public static boolean isUserOnline(Player player) {
        return isUserOnline(player.getUniqueId());
    }

    /*
     Regular sendMessage
     */
    public static void tell(CommandSource player, Text text, boolean addPrefix) {
        text = Text.builder().append(text).append(Text.of(addPrefix ? Translations.PREFIX.s() : "")).build();
        player.sendMessage(text);
    }

    public static void tell(CommandSource player, Text text) {
        tell(player, text, true);
    }

    public static void tell(CommandSource player, String message, boolean addPrefix) {
        tell(player, Text.of(Translations.PREFIX.s(), message, addPrefix));
    }

    public static void tell(CommandSource player, String message) {
        tell(player, Text.of(Translations.PREFIX.s(), message));
    }

    /*
     sendMessage if single player has permission
     */
    public static void tellIfHasPermission(CommandSource player, Text text, String permission, boolean addPrefix) {
        if (player.hasPermission(permission)) tell(player, text, addPrefix);
    }

    public static void tellIfHasPermission(CommandSource player, Text text, String permission) {
        tellIfHasPermission(player, text, permission, true);
    }

    public static void tellIfHasPermission(CommandSource player, String message, String permission, boolean addPrefix) {
        tellIfHasPermission(player, Text.of(message), permission, addPrefix);
    }

    public static void tellIfHasPermission(CommandSource player, String message, String permission) {
        tellIfHasPermission(player, message, permission, true);
    }

    /*
     sendMessage if online player(s) has/have permission
     */
    public static void broadcastForPermission(Text text, String permission, boolean addPrefix) {
        Sponge.getServer().getOnlinePlayers().forEach(p -> tellIfHasPermission(p, text, permission, addPrefix));
    }

    public static void broadcastForPermission(Text text, String permission) {
        broadcastForPermission(text, permission, true);
    }

    public static void broadcastForPermission(String message, String permission, boolean addPrefix) {
        broadcastForPermission(Text.of(message), permission, addPrefix);
    }

    public static void broadcastForPermission(String message, String permission) {
        broadcastForPermission(Text.of(message), permission, true);
    }

    /*
     Public broadcast
     */
    public static void broadcast(Text text, boolean addPrefix) {
        Sponge.getServer().getBroadcastChannel().send(Text.of((addPrefix ? Translations.PREFIX : ""), text));
    }

    public static void broadcast(Text text) {
        broadcast(text, true);
    }

    public static void broadcast(String message, boolean addPrefix) {
        broadcast(Text.of(message), addPrefix);
    }

    public static void broadcast(String message) {
        broadcast(Text.of(message), true);
    }

    /*
     Player transformations
     */
    private static UserStorageService getUserStorage() {
        Optional<UserStorageService> storageServiceOptional = Sponge.getServiceManager().provide(UserStorageService.class);
        return storageServiceOptional.orElse(null);
    }

    public static Optional<Player> getPlayerFromName(String name) {
        Optional<User> optionalUser = getUserStorage().get(name);
        return optionalUser.flatMap(User::getPlayer);
    }

    public static Optional<Player> getPlayerFromUUID(UUID uuid) {
        Optional<User> optionalUser = getUserStorage().get(uuid);
        return optionalUser.flatMap(User::getPlayer);
    }

    public static Optional<Player> getPlayerFromP2(PlotPlayer plotPlayer) {
        return getPlayerFromUUID(plotPlayer.getUUID());
    }

    public static Optional<UUID> getUUIDFromName(String name) {
        Optional<Player> optionalPlayer = getPlayerFromName(name);
        return optionalPlayer.map(Identifiable::getUniqueId);
    }

    public static Optional<String> getNameFromUUID(UUID uuid) {
        Optional<Player> optionalPlayer = getPlayerFromUUID(uuid);
        return optionalPlayer.map(User::getName);
    }

    /*
     Player data
     */
    public static Optional<String> getLastIP(User user, CoreUserDataModule userDataModule) {
        String ip = null;
        if (user.getPlayer().isPresent())
            ip = user.getPlayer().get().getConnection().getAddress().getAddress().toString();

        if (ip == null) return userDataModule.getLastIp();
        return Optional.empty();
    }

    public static Optional<Instant> getFirstPlayed(User user, CoreUserDataModule userDataModule) {
        Optional<Instant> i = user.get(Keys.FIRST_DATE_PLAYED);
        if (!i.isPresent()) return userDataModule.getFirstJoin();
        return Optional.empty();
    }

    public static Optional<Instant> getLastPlayed(User user) {
        if (user.isOnline()) return Optional.of(Instant.now());
        return user.get(Keys.LAST_DATE_PLAYED);
    }

    public static Optional<Location<World>> getLocationFromUser(User user) {
        if (user.isOnline() && user.getPlayer().isPresent())
            Optional.of(user.getPlayer().get().getLocation());

        if (user.getWorldUniqueId().isPresent()) {
            Optional<World> optionalWorld = Sponge.getServer().getWorld(user.getWorldUniqueId().get());
            if (optionalWorld.isPresent())
                return Optional.of(new Location<>(optionalWorld.get(), user.getPosition()));
        }

        return Optional.empty();
    }
}
