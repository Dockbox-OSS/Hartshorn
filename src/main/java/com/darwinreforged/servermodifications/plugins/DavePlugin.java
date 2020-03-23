package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.listeners.DaveChatListeners;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.DaveConfigurationUtil;
import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Plugin(id = "dave", name = "Darwin Chat Bot (Dave)", version = "2.0.1", description = "Read chat, send players a message if it picks up a configured message")
public class DavePlugin {

    @Inject
    private Logger logger;

    public DavePlugin() {
    }

    public Logger getLogger() {
        return logger;
    }

    private Properties settingsProperties = new Properties();
    private DaveConfigurationUtil settingsHandle;

    public Properties getSettingsProperties() {
        return settingsProperties;
    }

    private Properties messagesProperties = new Properties();
    private DaveConfigurationUtil messagesHandle;

    public Properties getMessagesProperties() {
        return messagesProperties;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path root;

    @Listener
    public void onServerFinishLoad(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new DaveChatListeners());
        Sponge.getCommandManager().register(this, daveMain, "dave");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        DavePluginWrapper.setSingleton(this);
        setupConfigurations();

    }

    private void setupConfigurations() {
        Map<String, String> defaultTriggers = new HashMap<String, String>() {{
            put("sample,trigger", "Sample response with <player> and <mention><>/triggercommand<>[https://trigger.website/]");
        }};
        settingsHandle = setupNewConfiguration("triggers.conf", messagesHandle, messagesProperties, defaultTriggers);

        Map<String, String> defaultMessages = new HashMap<String, String>() {{
            put("prefix", "&cDave : ");
            put("messageColor", "&f");
            put("discordChannel", "424884086230876161");
            put("muted", "examplePlayer,anotherPlayer");
        }};

        settingsHandle = setupNewConfiguration("dave.conf", settingsHandle, settingsProperties, defaultMessages);

        String[] mutedPlayers = settingsProperties.getProperty("muted").split(",");
        Collections.addAll(playerWhoMutedDave, mutedPlayers);
    }

    private DaveConfigurationUtil setupNewConfiguration(String child, DaveConfigurationUtil handler, Properties properties, Map<String, String> entries) {
        properties.clear();
        File propertyFile = new File(root.toFile(), child);
        boolean fileExisted = true;

        if (!propertyFile.exists()) {
            propertyFile.getParentFile().mkdirs();
            try {
                propertyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileExisted = false;
        }

        handler = new DaveConfigurationUtil(propertyFile);
        if (!fileExisted) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                handler.set(entry.getKey(), entry.getValue());
            }
        }

        handler.getAll().forEach(properties::setProperty);

        return handler;
    }

    private CommandSpec daveReload = CommandSpec.builder()
            .description(Text.of("Reload Daves config"))
            .permission("dave.reload")
            .executor(new reloadDave())
            .build();

    private CommandSpec daveMute = CommandSpec.builder()
            .description(Text.of("Mutes Dave"))
            .permission("dave.mute")
            .executor(new muteDave())
            .build();

    private CommandSpec daveMain = CommandSpec.builder()
            .description(Text.of("Main Dave command"))
            .child(daveReload, "reload")
            .child(daveMute, "mute")
            .build();

    public List<String> getPlayerWhoMutedDave() {
        return playerWhoMutedDave;
    }

    private List<String> playerWhoMutedDave = new ArrayList<>();

    private class muteDave implements CommandExecutor {
        @NotNull
        @Override
        public CommandResult execute(@NotNull CommandSource src, CommandContext args) {
            if (src instanceof Player) {
                String name = src.getName();
                if (!playerWhoMutedDave.contains(name)) {
                    playerWhoMutedDave.add(name);
                    PlayerUtils.tell(src, Translations.DAVE_MUTED.t());
                    String currentList = settingsHandle.get("muted");
                    String newList = currentList + "," + name;
                    settingsHandle.set("muted", newList);
                } else {
                    playerWhoMutedDave.remove(name);
                    PlayerUtils.tell(src, Translations.DAVE_UNMUTED.t());
                    String currentList = settingsHandle.get("muted");
                    String newList = currentList.replace("," + name, "");
                    settingsHandle.set("muted", newList);
                }
            }
            return CommandResult.success();
        }
    }

    private class reloadDave implements CommandExecutor {
        @NotNull
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            setupConfigurations();
            PlayerUtils.tell(src, Translations.DAVE_RELOADED_USER.ft(settingsProperties.getProperty("prefix")), false);
            return CommandResult.success();
        }
    }
}
