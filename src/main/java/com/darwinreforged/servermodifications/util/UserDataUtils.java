package com.darwinreforged.servermodifications.util;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.modules.core.datamodules.CoreUserDataModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

public class UserDataUtils {

  @Nullable
  static Text getIP(CommandSource source, User user, CoreUserDataModule userDataModule) {
    @Nullable String ip;
    ip = user.getPlayer().get().getConnection().getAddress().getAddress().toString();
    if (ip == null) {
        ip = userDataModule.getLastIp().get();
    }
    
    return Text.of(ip == null ? ip : "Unknown");
  }

  @Nullable
  static Text getFirstPlayed(CommandSource source, User user, CoreUserDataModule userDataModule) {
    Optional<Instant> i = user.get(Keys.FIRST_DATE_PLAYED);
    if (!i.isPresent()) {
      i = userDataModule.getFirstJoin();
    }

    return i.map(
            x ->
                Text.of(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(source.getLocale())
                        .withZone(ZoneId.systemDefault())
                        .format(x)))
        .orElse(null);
  }

  @Nullable
  static Text getLastPlayed(CommandSource source, User user) {
    if (user.isOnline()) {
      return null;
    }

    return user.get(Keys.LAST_DATE_PLAYED)
        .map(
            x ->
                Text.of(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(source.getLocale())
                        .withZone(ZoneId.systemDefault())
                        .format(x)))
        .orElse(null);
  }

  @Nullable
  static Text getLocation(CommandSource source, User user, CoreUserDataModule userDataModule) {
    if (user.isOnline()) {
      return getLocationString(
          "command.seen.currentlocation", user.getPlayer().get().getLocation(), source);
    }

    Optional<WorldProperties> wp =
        user.getWorldUniqueId().map(x -> Sponge.getServer().getWorldProperties(x).orElse(null));
    if (wp.isPresent()) {
      return getLocationString("command.seen.lastlocation", wp.get(), user.getPosition(), source);
    }

    // TODO: Remove - this is a fallback
    return userDataModule
        .getLogoutLocation()
        .map(worldLocation -> getLocationString("command.seen.lastlocation", worldLocation, source))
        .orElse(null);
  }

  static Text getLocationString(String key, Location<World> lw, CommandSource source) {
    return getLocationString(key, lw.getExtent().getProperties(), lw.getPosition(), source);
  }

  static Text getLocationString(
      String key, WorldProperties worldProperties, Vector3d position, CommandSource source) {
    return Text.of(worldProperties.getWorldName() + ", " + position.toInt().toString());
  }
}
