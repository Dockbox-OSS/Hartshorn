package com.darwinreforged.server.modules.optimizations.spectatorteleport;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.living.state.GameModes;
import com.darwinreforged.server.core.events.internal.PlayerTeleportEvent;
import com.darwinreforged.server.core.events.internal.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Module(id = "spectatorteleport", name = "Spectator Teleport", description = "Block teleportation for players who are in Spectator mode", authors = {"GuusLieben", "TheCrunchy"})
public class SpectatorTeleportModule {

    List<String> whitelistedWorlds = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Listener
    public void onServerStart(ServerStartedEvent event) {
        FileUtils fileUtils = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> yamlData = fileUtils.getConfigYamlData(this);
        if (yamlData.containsKey("whitelist")) {
            List<String> whitelist = (List<String>) yamlData.get("whitelist");
            whitelistedWorlds = whitelist;
        } else {
            yamlData = new HashMap<>();
            yamlData.put("whitelist", Arrays.asList("SampleWorld", "Another_World"));
            fileUtils.writeConfigYaml(yamlData, this);
        }
    }

    @Listener
    public void onTeleport(PlayerTeleportEvent event) {
        DarwinPlayer player = (DarwinPlayer) event.getTarget();
        if (player.getGameMode().equals(GameModes.SPECTATOR)) {
            player.getWorld().ifPresent(world -> {
                if (!whitelistedWorlds.contains(world.getName()) && !player.hasPermission(Permissions.ADMIN_BYPASS)) {
                    event.setCancelled(true);
                    player.tell(Translations.SPECTATOR_TP_DISALLOWED.s());
                }
            });
        }
    }

}
