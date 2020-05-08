package com.darwinreforged.server.modules.optimizations.worldunloader;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.events.internal.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.LocationUtils;
import com.darwinreforged.server.core.util.TimeUtils;
import com.darwinreforged.server.core.util.commands.annotation.Command;
import com.darwinreforged.server.core.util.commands.annotation.Description;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.annotation.Src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Module(id = "worldunloader", name = "WorldUnloader", description = "Unload worlds with no players in them", authors = "GuusLieben")
public class WorldUnloadModule {

    private FileUtils fileUtil;
    private final List<String> unloadBlacklist = new ArrayList<>();

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        fileUtil = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> configData = fileUtil.getConfigYamlData(this);
        if (configData.containsKey("blacklist")) {
            String[] configuredBlacklist = (String[]) configData.get("blacklist");
            unloadBlacklist.addAll(Arrays.asList(configuredBlacklist));
        }
        refreshBlackList();

        DarwinServer.getUtilChecked(TimeUtils.class).schedule()
                .async()
                .interval(2, TimeUnit.MINUTES)
                .execute(this::unloadTask)
                .submit();
    }

    @Command("wu <world>")
    @Permission(Permissions.WU_ADD)
    @Description("Add world to unload blacklist")
    public void addWorldCommand(@Src DarwinPlayer player, String world) {
        unloadBlacklist.add(world);
        refreshBlackList();
        player.tell(Translations.WU_ADDED.f(world));
    }

    private void refreshBlackList() {
        Map<String, Object> configData = new HashMap<>();
        configData.put("blacklist", unloadBlacklist.toArray());
        fileUtil.writeConfigYaml(configData, this);
    }

    private void unloadTask() {
        DarwinServer.getUtilChecked(LocationUtils.class)
                .getEmptyWorlds().stream()
                .filter(world -> !unloadBlacklist.contains(world.getName()))
                .forEach(DarwinWorld::unloadWorld);
    }
}
