package com.darwinreforged.server.modules.optimizations.spectatorteleport;

import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.state.GameModes;
import com.darwinreforged.server.core.events.internal.player.PlayerTeleportEvent;
import com.darwinreforged.server.core.events.internal.server.ServerReloadEvent;
import com.darwinreforged.server.core.events.internal.server.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 The type Spectator teleport module.
 */
@Module(id = "spectatorteleport", name = "Spectator Teleport", description = "Block teleportation for players who are in Spectator mode", authors = {"GuusLieben", "TheCrunchy"})
public class SpectatorTeleportModule {

    /**
     The Whitelisted worlds.
     */
    List<String> whitelistedWorlds = new ArrayList<>();

    /**
     On server reload.

     @param event
     the event
     */
    @Listener
    public void onServerReload(ServerReloadEvent event) {
        init();
    }

    /**
     On server start.

     @param event
     the event
     */
    @Listener
    public void onServerStart(ServerStartedEvent event) {
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        FileUtils fileUtils = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> yamlData = fileUtils.getYamlDataForConfig(this);
        if (yamlData.containsKey("whitelist")) {
            whitelistedWorlds = (List<String>) yamlData.get("whitelist");
        } else {
            yamlData = new HashMap<>();
            yamlData.put("whitelist", Arrays.asList("SampleWorld", "Another_World"));
            fileUtils.writeYamlDataForConfig(yamlData, this);
        }
    }

    /**
     On teleport.

     @param event
     the event
     */
    @Listener
    public void onTeleport(PlayerTeleportEvent event) {
        DarwinPlayer player = (DarwinPlayer) event.getTarget();
        if (player.getGameMode().equals(GameModes.SPECTATOR)) {
            player.getWorld().ifPresent(world -> {
                if (!whitelistedWorlds.contains(world.getName()) && player.hasPermission(Permissions.ADMIN_BYPASS)) {
                    event.setCancelled(true);
                    player.sendMessage(Translations.SPECTATOR_TP_DISALLOWED.s());
                }
            });
        }
    }

}
