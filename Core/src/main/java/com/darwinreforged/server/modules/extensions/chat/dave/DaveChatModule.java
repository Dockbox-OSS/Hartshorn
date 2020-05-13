package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.types.living.DarwinPlayer;
import com.darwinreforged.server.core.events.internal.server.ServerInitEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 The type Dave chat module.
 */
@Command(aliases = "dave", usage = "/dave [mute|reload]", desc = "Dave commands")
@Module(id = "dave", name = "Dave", version = "2.0.1", description = "Read chat, send players a message if it picks up a configured message", authors = "GuusLieben")
public class DaveChatModule {

    /**
     Instantiates a new Dave chat module.
     */
    public DaveChatModule() {
    }

    private DaveConfigurationUtil configurationUtil;

    /**
     Gets configuration util.

     @return the configuration util
     */
    public DaveConfigurationUtil getConfigurationUtil() {
        return configurationUtil;
    }

    /**
     On server init.

     @param event
     the event
     */
    @Listener
    public void onServerInit(ServerInitEvent event) {
        DarwinServer.getServer().registerListener(new DaveChatListeners());
        setupConfigurations();
    }

    private void setupConfigurations() {
        configurationUtil = new DaveConfigurationUtil();
    }

    /**
     Gets player who muted dave.

     @return the player who muted dave
     */
    public List<UUID> getPlayerWhoMutedDave() {
        return playerWhoMutedDave;
    }

    private final List<UUID> playerWhoMutedDave = new ArrayList<>();

    /**
     Mute.

     @param src
     the src
     */
    @Command(aliases = "mute", usage = "dave mute", desc = "Mutes or unmutes Dave for the executing player")
    @Permission(Permissions.DAVE_MUTE)
    public void mute(DarwinPlayer src) {
        if (!configurationUtil.getMuted().contains(src.getUniqueId())) {
            configurationUtil.getMuted().add(src.getUniqueId());
            src.sendMessage(Translations.DAVE_MUTED.s());
        } else {
            playerWhoMutedDave.remove(src.getUniqueId());
            src.sendMessage(Translations.DAVE_UNMUTED.s());
        }
    }

    /**
     Reload.

     @param src
     the src
     */
    @Command(aliases = "reload", usage = "dave reload", desc = "Reloads Dave")
    @Permission(Permissions.DAVE_RELOAD)
    public void reload(DarwinPlayer src) {
        setupConfigurations();
        src.sendMessage(Translations.DAVE_RELOADED_USER.f(configurationUtil.getPrefix().getText()));
    }
}
