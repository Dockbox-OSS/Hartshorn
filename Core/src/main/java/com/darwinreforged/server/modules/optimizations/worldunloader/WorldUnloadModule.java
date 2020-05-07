package com.darwinreforged.server.modules.optimizations.worldunloader;

import com.darwinreforged.server.core.events.internal.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Module(id = "worldunloader", name = "WorldUnloader", description = "Unload worlds with no players in them", authors = "GuusLieben")
public class WorldUnloadModule {

    private FileUtils fileUtil;
    private final List<String> unloadBlacklist = new ArrayList<>();

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        fileUtil = DarwinServer.getUtilChecked(FileUtils.class);
        Map<String, Object> configData = fileUtil.getYamlData(this);
        if (configData.containsKey("blacklist")) {
            String[] configuredBlacklist = (String[]) configData.get("blacklist");
            unloadBlacklist.addAll(Arrays.asList(configuredBlacklist));
        }
        configData = new HashMap<>();
        configData.put("blacklist", unloadBlacklist.toArray());
        fileUtil.writeYaml(configData, this);
    }

    private void eh() {
        fileUtil.getYamlData(this);
        unloadBlacklist.forEach(System.out::println);
    }

}
