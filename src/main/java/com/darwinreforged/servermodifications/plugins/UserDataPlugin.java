package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.WorldUtil;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.dataservices.modular.ModularUserService;
import io.github.nucleuspowered.nucleus.modules.core.datamodules.CoreUserDataModule;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Plugin(
    id = "userdata",
    name = "UserData",
    version = "0.0.1",
    description = "Collect user data from several plugins and display them to the executing player",
    authors = {"DiggyNevs"})
public class UserDataPlugin {

  public UserDataPlugin() {
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    CommandSpec spec =
        CommandSpec.builder()
            .arguments(GenericArguments.string(Text.of("player")))
            .permission("darwin.playerdata")
            .executor(new UserDataExecutor())
            .build();

    Sponge.getCommandManager().register(this, spec, "data");
  }

  private static class UserDataExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<String> playerOpt = args.getOne("player");
      if (playerOpt.isPresent()) {
        Optional<UserStorageService> userStorage =
            Sponge.getServiceManager().provide(UserStorageService.class);
        String username = playerOpt.get();
        Optional<User> optSpongePlayer = userStorage.get().get(username);

        if (optSpongePlayer.isPresent()) {
          User user = optSpongePlayer.get();

          PlotPlayer plotPlayer = UUIDHandler.getPlayer(user.getUniqueId());
          boolean showPlots = plotPlayer != null;

          src.sendMessage(
                  Text.of(
                          Translations.USER_DATA_HEADER.f(username),
                          getPlotSquaredText(plotPlayer, showPlots),
                          getNucleusText(user, src),
                          getLuckpermsText(user)
                  ));

        } else {
          PlayerUtils.tell(src, Translations.USER_DATA_FAILED_COLLECT.f(username));
        }
      }
      return CommandResult.success();
    }

    private Text getPlotSquaredText(PlotPlayer player, boolean show) {
      Text.Builder builder =
          Text.builder()
              .append(Text.NEW_LINE)
              .append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "⋙ PlotSquared"))
              .append(Text.NEW_LINE);

      if (show) {
        AtomicInteger worlds = new AtomicInteger();
        AtomicInteger plots = new AtomicInteger();
        player
            .getPlots()
            .forEach(
                plot -> {
                  if (plot.getWorldName().replaceAll(",", ";").equals(plot.getId().toString()))
                    worlds.getAndIncrement();
                  else plots.getAndIncrement();
                });

        return builder
            .append(Text.of(TextColors.DARK_AQUA, "Worlds: ", TextColors.AQUA, worlds))
            .append(Text.NEW_LINE)
            .append(Text.of(TextColors.DARK_AQUA, "Plots: ", TextColors.AQUA, plots))
            .build();
      } else {
        return builder
            .append(
                Text.of(TextColors.GRAY, TextStyles.ITALIC, "Not available if player is offline"))
            .build();
      }
    }

    private static String timeConversion(int totalSeconds) {

      final int MINUTES_IN_AN_HOUR = 60;
      final int SECONDS_IN_A_MINUTE = 60;

      int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
      int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
      int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
      int hours = totalMinutes / MINUTES_IN_AN_HOUR;

      return hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }

    private Text getLuckpermsText(User player) {
      me.lucko.luckperms.api.User user = LuckPerms.getApi().getUser(player.getUniqueId());

      if (user != null) {
        return Text.builder()
            .append(Text.NEW_LINE)
            .append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "⋙ LuckPerms"))
            .append(Text.NEW_LINE)
            .append(
                Text.of(
                    TextColors.DARK_AQUA,
                    "Primary group: ",
                    TextColors.AQUA,
                    user.getPrimaryGroup()))
            .append(Text.NEW_LINE)
            .append(
                Text.of(
                    TextColors.DARK_AQUA,
                    "Prefix: ",
                    TextColors.AQUA,
                    user.getCachedData().getMetaData(Contexts.global()).getPrefix()))
            .build();
      }
      return Text.EMPTY;
    }

    private Text getNucleusText(User player, CommandSource src) {
      Optional<ModularUserService> optUserService =
          Nucleus.getNucleus().getUserDataManager().get(player);
      CoreUserDataModule core = optUserService.get().get(CoreUserDataModule.class);

      String ip = core.getLastIp().get();
      if (Arrays.stream(ip.split("\\."))
          .anyMatch(x -> Integer.parseInt(x.replaceAll("\\\\", "").replaceAll("/", "")) > 255)) {
        src.sendMessage(
            Text.of(
                TextColors.DARK_GRAY,
                "[] ",
                TextColors.RED,
                "Could not collect data for ",
                TextColors.DARK_RED,
                player.getName()));
      }

      UserStorageService uss =
          Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
      List<User> users =
          Nucleus.getNucleus().getUserCacheService().getForIp(ip).stream()
              .map(uss::get)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toList());
      List<String> usernames = users.stream().map(User::getName).collect(Collectors.toList());
      String alts = usernames.isEmpty() ? "None" : String.join(", ", usernames);

      return Text.builder()
          .append(Text.NEW_LINE)
          .append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "⋙ Nucleus"))
          .append(Text.NEW_LINE)
          .append(
              Text.of(
                  TextColors.DARK_AQUA,
                  "First joined: ",
                  TextColors.AQUA,
                  core.getFirstJoin().get()))
          .append(Text.NEW_LINE)
          .append(
              Text.of(
                  TextColors.DARK_AQUA, "Last known IP: ", TextColors.AQUA, core.getLastIp().get()))
          .append(Text.NEW_LINE)
          .append(
              Text.of(
                  TextColors.DARK_AQUA,
                  "Last known name: ",
                  TextColors.AQUA,
                  core.getLastKnownName().get()))
          .append(Text.NEW_LINE)
          .append(
              Text.of(
                  TextColors.DARK_AQUA, "Last login: ", TextColors.AQUA, core.getLastLogin().get()))
          .append(Text.NEW_LINE)
          .append(
              Text.of(
                  TextColors.DARK_AQUA,
                  "Last logout: ",
                  TextColors.AQUA,
                  core.getLastLogout().get()))
          .append(Text.NEW_LINE)
          .append(
              Text.of(
                  TextColors.DARK_AQUA,
                  "Last seen: ",
                  TextColors.AQUA,
                  WorldUtil.IMP.getLastSeen(Collections.singleton(player.getUniqueId()))))
          .append(Text.NEW_LINE)
          .append(Text.of(TextColors.DARK_AQUA, "Alt accounts: ", TextColors.AQUA, alts))
          .build();
    }
  }
}
