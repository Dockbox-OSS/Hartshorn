package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.events.internal.server.ServerInitEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.commands.annotation.Command;
import com.darwinreforged.server.core.util.commands.annotation.Description;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.annotation.Src;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Module(id = "dave", name = "Dave", version = "2.0.1", description = "Read chat, send players a message if it picks up a configured message", authors = "GuusLieben")
public class DaveChatModule {

    public DaveChatModule() {
    }

    private DaveConfigurationUtil configurationUtil;

    public DaveConfigurationUtil getConfigurationUtil() {
        return configurationUtil;
    }

    @Listener
    public void onServerInit(ServerInitEvent event) {
        DarwinServer.getServer().registerListener(new DaveChatListeners());
        setupConfigurations();
    }

    private void setupConfigurations() {
        configurationUtil = new DaveConfigurationUtil();
    }

    public List<UUID> getPlayerWhoMutedDave() {
        return playerWhoMutedDave;
    }

    private final List<UUID> playerWhoMutedDave = new ArrayList<>();

    @Command("dave mute")
    @Permission(Permissions.DAVE_MUTE)
    @Description("Mutes or unmutes Dave for the executing player")
    public void mute(@Src DarwinPlayer src) {
        if (!configurationUtil.getMuted().contains(src.getUniqueId())) {
            configurationUtil.getMuted().add(src.getUniqueId());
            src.tell(Translations.DAVE_MUTED.s());
        } else {
            playerWhoMutedDave.remove(src.getUniqueId());
            src.tell(Translations.DAVE_UNMUTED.s());
        }
    }

    @Command("dave reload")
    @Permission(Permissions.DAVE_RELOAD)
    @Description("Reloads Dave triggers and config")
    public void reload(@Src DarwinPlayer src) {
        setupConfigurations();
        src.tell(Translations.DAVE_RELOADED_USER.f(configurationUtil.getPrefix().getText()));
    }
}
