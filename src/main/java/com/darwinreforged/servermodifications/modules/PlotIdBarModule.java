package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.listeners.PlotIDBarMoveEventListener;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.objects.PlotIDBarPlayer;
import com.darwinreforged.servermodifications.objects.PlotIDBarRootConfig;
import com.darwinreforged.servermodifications.objects.PlotIDToggled;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.FileManager;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@ModuleInfo(id = "plotidbossbar", name = "Darwin PlotID boss bar", version = "1.0", description = "Plot ID boss bar")
public class PlotIdBarModule extends PluginModule {
    public PlotIdBarModule() {
    }

    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new PlotIDBarMoveEventListener());
        Sponge.getCommandManager().register(this, toggle, "toggle");
    }

    public static ArrayList<UUID> toggledID = new ArrayList<>();
    public static ArrayList<UUID> toggledMembers = new ArrayList<>();
    public static HashMap<UUID, PlotIDBarPlayer> allPlayers = new HashMap<>();

    @Override
    public void onServerStart(GameStartedServerEvent event) {
        File file = new File(FileManager.getConfigDirectory(this).toFile(), "toggled.conf");

        if (!file.exists()) {
            PlotIDToggled plotIDToggled = new PlotIDToggled();
            plotIDToggled.setName("Toggled people");
            PlotIDBarRootConfig config = new PlotIDBarRootConfig();
            config.getCategories().add(plotIDToggled);
            saveConfig(config, file.toPath());
        }
        PlotIDBarRootConfig rootConfig = loadConfig(file.toPath());
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
            ObjectMapper<PlotIDBarRootConfig> mapper = ObjectMapper.forClass(PlotIDBarRootConfig.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(path).build();
            return mapper.bind(new PlotIDBarRootConfig()).populate(hcl.load());
        } catch (Exception e) {
            throw new RuntimeException("Could not load file " + path, e);
        }
    }

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
            Optional<PlotIdBarModule> moduleOptional = DarwinServer.getModule(PlotIdBarModule.class);
            if (moduleOptional.isPresent()) {
                File file = new File(FileManager.getConfigDirectory(moduleOptional.get()).toFile(), "toggled.conf");
                PlotIDBarPlayer barP = new PlotIDBarPlayer(player);
                if (toggledID.contains(player.getUniqueId())) {
                    toggledID.remove(player.getUniqueId());
                    PlayerUtils.tell(player, Translations.PID_TOGGLE_BAR.ft(Translations.DEFAULT_ON));
                    barP.setBarBool(false);
                } else {
                    toggledID.add(player.getUniqueId());
                    PlayerUtils.tell(player, Translations.PID_TOGGLE_BAR.ft(Translations.DEFAULT_OFF));
                    barP.setBarBool(true);
                }
                allPlayers.put(player.getUniqueId(), barP);
                PlotIDToggled plotIDToggled = new PlotIDToggled();
                PlotIDBarRootConfig config = new PlotIDBarRootConfig();
                plotIDToggled.setToggledID(toggledID);
                plotIDToggled.setToggledMem(toggledMembers);
                config.getCategories().add(plotIDToggled);
                saveConfig(config, file.toPath());
            }
            return CommandResult.success();
        }
    }


    public class TogglePlotMembers implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<PlotIdBarModule> moduleOptional = DarwinServer.getModule(PlotIdBarModule.class);
            if (moduleOptional.isPresent()) {
                File file = new File(FileManager.getConfigDirectory(moduleOptional.get()).toFile(), "toggled.conf");
                Player player = (Player) src;
                PlotIDBarPlayer barP = new PlotIDBarPlayer(player);
                if (toggledMembers.contains(player.getUniqueId())) {
                    toggledMembers.remove(player.getUniqueId());
                    PlayerUtils.tell(player, Translations.PID_TOGGLE_MEMBERS.ft(Translations.DEFAULT_ON));
                    barP.setMembersBool(false);
                } else {
                    toggledMembers.add(player.getUniqueId());
                    PlayerUtils.tell(player, Translations.PID_TOGGLE_MEMBERS.ft(Translations.DEFAULT_OFF));
                    barP.setMembersBool(true);
                }
                allPlayers.put(player.getUniqueId(), barP);
                PlotIDToggled plotIDToggled = new PlotIDToggled();
                PlotIDBarRootConfig config = new PlotIDBarRootConfig();
                plotIDToggled.setToggledID(toggledID);
                plotIDToggled.setToggledMem(toggledMembers);
                config.getCategories().add(plotIDToggled);
                saveConfig(config, file.toPath());
            }
            return CommandResult.success();
        }
    }
}
