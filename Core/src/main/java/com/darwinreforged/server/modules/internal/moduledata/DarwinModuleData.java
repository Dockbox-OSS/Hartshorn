package com.darwinreforged.server.modules.internal.moduledata;

import com.darwinreforged.server.core.events.internal.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.modules.PluginModule;
import com.darwinreforged.server.core.util.FileUtils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(id = "darwinmoduledata", name = "Darwin Module Data", version = "0.0.2", description = "Collects and stores plugin data", authors = {"GuusLieben"})
public class DarwinModuleData extends PluginModule {

    private final Map<String, Map<String, Object>> data = new HashMap<>();

    public DarwinModuleData() {
    }

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        File dataFile = new File(DarwinServer.getUtilChecked(FileUtils.class).getDataDirectory(this).toFile(), "module_data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        DarwinServer.getAllModuleInfo().forEach(this::registerPlugin);
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(dataFile);
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerPlugin(ModuleInfo module) {
        // Data storage before Yaml conversion
        Map<String, Object> data = new HashMap<>();

        // Generic data
        data.put("id", module.id());
        data.put("name", module.name());
        data.put("version", module.version());
        data.put("description", module.description());
        data.put("url", module.url());
        data.put("authors", module.authors());
        data.put("source", module.source());

        // Write plugin data to unique file
        this.data.put(module.id(), data);
    }
}
