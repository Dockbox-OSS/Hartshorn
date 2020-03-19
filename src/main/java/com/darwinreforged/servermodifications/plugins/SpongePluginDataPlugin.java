package com.darwinreforged.servermodifications.plugins;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Plugin(id = "spongeplugindata", name = "Sponge Plugin Data", version = "0.0.2", description = "Collects and stores plugin data", authors = {"DiggyNevs"})
public class SpongePluginDataPlugin {

    @Inject
    private Logger logger;

    private Map<String, Map<String, Object>> data = new HashMap<>();
    private static final File dataFile = Sponge.getGame().getSavesDirectory().resolve("data/spongeplugindata/plugin_data.yml").toFile();

    public SpongePluginDataPlugin() {
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        if (!dataFile.exists()) {
            try {
                dataFile.mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Listener
    public void onServerFinishLoad(GameInitializationEvent event) {
        Sponge.getPluginManager().getPlugins().forEach(this::registerPlugin);
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(dataFile);
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerPlugin(PluginContainer container) {
        // Data storage before Yaml conversion
        Map<String, Object> data = new HashMap<>();

        // Generic data
        data.put("id", container.getId());
        if (container.getVersion().isPresent())
            data.put("version", container.getVersion().get());
        if (container.getDescription().isPresent())
            data.put("description", container.getDescription().get());
        if (container.getUrl().isPresent())
            data.put("url", container.getUrl().get());
        if (!container.getAuthors().isEmpty())
            data.put("authors", container.getAuthors());

        // Dependencies, id and version
        List<String> dependencies = container.getDependencies().stream().map(dep -> (dep.getId() + "::" + (dep.getVersion() == null ? "any" : dep.getVersion()))).collect(Collectors.toList());
        data.put("dependencies", dependencies);

        // Source jar for the plugin
        Optional<Path> optionalPath = container.getSource();
        String source = "unknown";
        if (optionalPath.isPresent()) source = optionalPath.get().getFileName().toString();
        data.put("source", source);
        if (container.getInstance().isPresent())
            data.put("main_class", container.getInstance().get().getClass().getName());
        data.put("instance", container.getClass().getName());

        // Write plugin data to unique file
        this.data.put(container.getId(), data);
    }
}
