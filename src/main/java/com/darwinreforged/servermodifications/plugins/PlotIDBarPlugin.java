package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.listeners.PlotIDBarMoveEventListener;
import com.darwinreforged.servermodifications.objects.PlotIDBarPlayer;
import com.darwinreforged.servermodifications.objects.PlotIDBarRootConfig;
import com.darwinreforged.servermodifications.objects.PlotIDToggled;
import com.google.inject.Inject;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(id = "plotidbossbar", name = "Darwin PlotID boss bar", version = "1.0", description = "Plot ID boss bar")
public class PlotIDBarPlugin {
    public PlotIDBarPlugin() {
    }

    @Listener
    public void onServerFinishLoad(GameStartedServerEvent event) {
        Sponge.getEventManager().registerListeners(this, new PlotIDBarMoveEventListener());
        Sponge.getCommandManager().register(this, toggle, "toggle");
        plugin = this;
    }

    Object plugin;
    public static ArrayList<UUID> toggledID = new ArrayList<>();
    public static ArrayList<UUID> toggledMembers = new ArrayList<>();
    public static HashMap<UUID, PlotIDBarPlayer> allPlayers = new HashMap<>();

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Task removeOffline = Task.builder().execute(new clearOfflines())
                .delayTicks(1)
                .interval(1, TimeUnit.MINUTES)
                .name("Remove offline players from my map").submit(this);
        File file = new File(root.toFile(), "toggled.conf");

        if (!file.exists()) {
            PlotIDToggled plotIDToggled = new PlotIDToggled();
            plotIDToggled.setName("Toggled people");
            PlotIDBarRootConfig config = new PlotIDBarRootConfig();
            config.getCategories().add(plotIDToggled);
            saveConfig(config, file.toPath());
        }
        rootConfig = loadConfig(file.toPath());
        for (PlotIDToggled plotIDToggled : rootConfig.getCategories()) {
            toggledID = (ArrayList<UUID>) plotIDToggled.getToggledID();
            toggledMembers = (ArrayList<UUID>) plotIDToggled.getToggledMem();
        }
    }

    private void saveConfig(PlotIDBarRootConfig config, Path path) {
        try {
            if (!path.toFile().getParentFile().exists()) {
                Files.createDirectories(path.toFile().getParentFile().toPath());
            }
            ObjectMapper.BoundInstance configMapper = ObjectMapper.forObject(config);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(path).build();
            SimpleConfigurationNode scn = SimpleConfigurationNode.root();
            configMapper.serialize(scn);
            hcl.save(scn);
        } catch (Exception e) {
            throw new RuntimeException("Could not write file. ", e);
        }
    }

    private PlotIDBarRootConfig loadConfig(Path path) {
        try {
            logger.info("Loading config...");
            ObjectMapper<PlotIDBarRootConfig> mapper = ObjectMapper.forClass(PlotIDBarRootConfig.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(path).build();
            return mapper.bind(new PlotIDBarRootConfig()).populate(hcl.load());
        } catch (Exception e) {
            throw new RuntimeException("Could not load file " + path, e);
        }
    }

    public class clearOfflines implements Runnable {
        public void run() {
            ArrayList<Player> offlinePlayers = new ArrayList<Player>();
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "Clearing offline players from PlotID maps"));
            for (Entry<UUID, PlotIDBarPlayer> barP : allPlayers.entrySet()) {
                if (!barP.getValue().getPlayer().isOnline()) {
                    offlinePlayers.add(barP.getValue().getPlayer());
                }
            }
            for (Player player : offlinePlayers) {
                allPlayers.remove(player.getUniqueId());
                Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "Clearing ", player.getName()));
            }
            offlinePlayers.clear();
        }
    }

    private PlotIDBarRootConfig rootConfig;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path root;


    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;


    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)

    private Path privateConfigDir;

    public static Optional<User> getUser(UUID owner) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(owner);
    }

    CommandSpec plotmemtoggle = CommandSpec.builder()
            .description(Text.of("Toggle for Plot members"))
            .permission("DarwinPlotID.Toggle")
            .executor(new TogglePlotMembers())
            .build();
    CommandSpec plotidtoggle = CommandSpec.builder()
            .description(Text.of("Toggle for Plot ID"))
            .permission("DarwinPlotID.Toggle")
            .executor(new TogglePlotID())
            .build();

    CommandSpec toggle = CommandSpec.builder()
            .description(Text.of("Toggle main command"))
            .permission("DarwinPlotID.Toggle")
            .child(plotidtoggle, "id", "bar")
            .child(plotmemtoggle, "member")
            .build();

    public class TogglePlotID implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Player player = (Player) src;
            File file = new File(root.toFile(), "toggled.conf");
            PlotIDBarPlayer barP = new PlotIDBarPlayer(player);
            if (toggledID.contains(player.getUniqueId())) {
                toggledID.remove(player.getUniqueId());
                player.sendMessage(Text.of(TextColors.WHITE, "Updating PlotID Bar Preference to ", TextColors.RED, "on"));
                barP.setBarBool(false);
            } else {
                toggledID.add(player.getUniqueId());
                player.sendMessage(Text.of(TextColors.WHITE, "Updating PlotID Bar Preference to ", TextColors.RED, "off"));
                barP.setBarBool(true);
            }
            allPlayers.put(player.getUniqueId(), barP);
            PlotIDToggled plotIDToggled = new PlotIDToggled();
            PlotIDBarRootConfig config = new PlotIDBarRootConfig();
            plotIDToggled.setToggledID(toggledID);
            plotIDToggled.setToggledMem(toggledMembers);
            config.getCategories().add(plotIDToggled);
            saveConfig(config, file.toPath());
            return CommandResult.success();
        }
    }


    public class TogglePlotMembers implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            File file = new File(root.toFile(), "toggled.conf");
            Player player = (Player) src;
            PlotIDBarPlayer barP = new PlotIDBarPlayer(player);
            if (toggledMembers.contains(player.getUniqueId())) {
                toggledMembers.remove(player.getUniqueId());
                player.sendMessage(Text.of(TextColors.WHITE, "Updating PlotID Members Preference to ", TextColors.RED, "on"));
                barP.setMembersBool(false);
            } else {
                toggledMembers.add(player.getUniqueId());
                player.sendMessage(Text.of(TextColors.WHITE, "Updating PlotID Members Preference to ", TextColors.RED, "off"));
                barP.setMembersBool(true);
            }
            allPlayers.put(player.getUniqueId(), barP);
            PlotIDToggled plotIDToggled = new PlotIDToggled();
            PlotIDBarRootConfig config = new PlotIDBarRootConfig();
            plotIDToggled.setToggledID(toggledID);
            plotIDToggled.setToggledMem(toggledMembers);
            config.getCategories().add(plotIDToggled);
            saveConfig(config, file.toPath());
            return CommandResult.success();
        }
    }
}
