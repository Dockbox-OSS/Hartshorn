package com.darwinreforged.server.modules.optimizations.gotolobby;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.living.state.GameModes;
import com.darwinreforged.server.core.events.internal.player.PlayerMoveEvent;
import com.darwinreforged.server.core.events.internal.server.ServerReloadEvent;
import com.darwinreforged.server.core.events.internal.server.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Module(id = "gotolobby", name = "GoToLobby", description = "Send players to the lobby if they are in a disallowed world", authors = "GuusLieben")
public class GoToLobbyModule {

    List<String> blacklist = new ArrayList<>();

    @Listener
    public void onServerReload(ServerReloadEvent event) {
        init();
    }

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        FileUtils fileUtils = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> config = fileUtils.getYamlDataForConfig(this);

        if(config.containsKey("blacklist")) blacklist = (List<String>) config.get("blacklist");
        else config.put("blacklist", Arrays.asList("denied_world", "worlds"));
        fileUtils.writeYamlDataForConfig(config, this);
    }

    @Listener
    public void onPlayerMove(PlayerMoveEvent event) {
        DarwinPlayer player = (DarwinPlayer) event.getTarget();
        player.getWorld().ifPresent(world -> {
            if (blacklist.contains(world.getName()) && !player.hasPermission(Permissions.ADMIN_BYPASS)) {
                player.setGameMode(GameModes.CREATIVE);
                player.execute("hub");
            }
        });
    }

}
