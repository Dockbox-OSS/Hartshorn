package com.darwinreforged.server.modules.optimizations.worldunloader;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.commands.annotations.Source;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.types.living.DarwinPlayer;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.events.internal.server.ServerReloadEvent;
import com.darwinreforged.server.core.events.internal.server.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.LocationUtils;
import com.darwinreforged.server.core.util.PlotUtils;
import com.darwinreforged.server.core.util.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 The type World unload module.
 */
@Module(id = "worldunloader", name = "WorldUnloader", description = "Unload worlds with no players in them", authors = "GuusLieben")
public class WorldUnloadModule {

    private FileUtils fileUtil;
    private PlotUtils plotUtils;
    private final List<String> unloadBlacklist = new ArrayList<>();

    /**
     On reload.

     @param event
     the event
     */
    @Listener
    public void onReload(ServerReloadEvent event) {
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
        fileUtil = DarwinServer.getUtilChecked(FileUtils.class);
        plotUtils = DarwinServer.getUtilChecked(PlotUtils.class);
        ArrayList<String> blacklist = (ArrayList<String>) fileUtil.getYamlDataForConfig(this, "blacklist", ArrayList.class);
        if (blacklist != null) unloadBlacklist.addAll(blacklist);
        refreshBlackList();

        // Do not use async, certain platforms do not allow async chunk modifications
        DarwinServer.getUtilChecked(TimeUtils.class).schedule()
                .interval(2, TimeUnit.MINUTES)
                .execute(this::unloadTask)
                .submit();
    }

    /**
     Add world command.

     @param player
     the player
     @param context
     the context
     @param world
     the world
     */
    @Command(
            aliases = "wu",
            desc = "Registers the given world to be blacklisted from unloading",
            max = 1,
            args = "world",
            usage = "wu [world]"
    )
    @Permission(Permissions.WU_ADD)
    public void addWorldCommand(DarwinPlayer player, CommandContext context, @Source DarwinWorld world) {
        String worldName;
        if (context.getArgumentCount() == 0) worldName = world.getName();
        else {
            Optional<DarwinWorld> worldCandidate = context.getArgumentAsWorld("world");
            if (worldCandidate.isPresent()) worldName = worldCandidate.get().getName();
            else {
                player.sendMessage(Translations.WORLD_NOT_FOUND.s());
                return;
            }
        }
        unloadBlacklist.add(worldName);
        refreshBlackList();
        player.sendMessage(Translations.WU_ADDED.f(world));
    }

    private void refreshBlackList() {
        Map<String, Object> configData = new HashMap<>();
        configData.put("blacklist", unloadBlacklist.toArray());
        fileUtil.writeYamlDataForConfig(configData, this);
    }

    private void unloadTask() {
        DarwinServer.getUtilChecked(LocationUtils.class)
                .getEmptyWorlds().stream()
                .filter(world -> !unloadBlacklist.contains(world.getName()) && !plotUtils.isPlotWorld(world))
                .forEach(darwinWorld -> darwinWorld.unloadWorld(false));
    }
}
