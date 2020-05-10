package com.darwinreforged.server.modules.optimizations.worldunloader;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.events.internal.server.ServerReloadEvent;
import com.darwinreforged.server.core.events.internal.server.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.LocationUtils;
import com.darwinreforged.server.core.util.PlotUtils;
import com.darwinreforged.server.core.util.TimeUtils;
import com.darwinreforged.server.core.util.commands.annotation.Command;
import com.darwinreforged.server.core.util.commands.annotation.Description;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.annotation.Src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Module(id = "worldunloader", name = "WorldUnloader", description = "Unload worlds with no players in them", authors = "GuusLieben")
public class WorldUnloadModule {

    private FileUtils fileUtil;
    private PlotUtils plotUtils;
    private final List<String> unloadBlacklist = new ArrayList<>();

    @Listener
    public void onReload(ServerReloadEvent event) {
        init();
    }

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        fileUtil = DarwinServer.getUtilChecked(FileUtils.class);
        plotUtils = DarwinServer.getUtilChecked(PlotUtils.class);
        ArrayList<String> blacklist = (ArrayList<String>) fileUtil.getConfigYamlData(this, "blacklist", ArrayList.class);
        if (blacklist != null) unloadBlacklist.addAll(blacklist);
        refreshBlackList();

        // Do not use async, certain platforms do not allow async chunk modifications
        DarwinServer.getUtilChecked(TimeUtils.class).schedule()
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
                .filter(world -> !unloadBlacklist.contains(world.getName()) && !plotUtils.isPlotWorld(world))
                .forEach(DarwinWorld::unloadWorld);
    }
}
