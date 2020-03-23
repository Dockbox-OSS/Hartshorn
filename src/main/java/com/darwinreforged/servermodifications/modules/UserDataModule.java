package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
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

@ModuleInfo(
    id = "userdata",
    name = "UserData",
    version = "0.0.1",
    description = "Collect user data from several plugins and display them to the executing player",
    authors = {"DiggyNevs"})
public class UserDataModule extends PluginModule {

  public UserDataModule() {
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

          PlayerUtils.tell(src, Text.of(
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
                .append(Translations.PLAYER_DATA_PLOTSQUARED.ft(worlds, plots))
            .build();
      } else {
        return builder
                .append(Translations.DATA_UNAVAILABLE_OFFLINE_PLAYER.t())
            .build();
      }
    }

    private Text getLuckpermsText(User player) {
      me.lucko.luckperms.api.User user = LuckPerms.getApi().getUser(player.getUniqueId());

      if (user != null) {
        return Text.builder()
                .append(Text.NEW_LINE)
                .append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "⋙ LuckPerms"))
                .append(Text.NEW_LINE)
                .append(Translations.PLAYER_DATA_LUCKPERMS.ft(
                        user.getPrimaryGroup(),
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
        PlayerUtils.tell(src, Translations.PLAYER_DATA_COLLECT_ERROR.ft(player.getName()));
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
      String alts = usernames.isEmpty() ? Translations.NONE.s() : String.join(", ", usernames);

      return Text.builder()
              .append(Text.NEW_LINE)
              .append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "⋙ Nucleus"))
              .append(Text.NEW_LINE)
              .append(Translations.PLAYER_DATA_NUCLEUS.ft(
                      core.getFirstJoin().get(),
                      core.getLastIp().get(),
                      core.getLastKnownName().get(),
                      core.getLastLogin().get(),
                      core.getLastLogout().get(),
                      WorldUtil.IMP.getLastSeen(Collections.singleton(player.getUniqueId())),
                      alts
              ))
          .build();
    }
  }
}
