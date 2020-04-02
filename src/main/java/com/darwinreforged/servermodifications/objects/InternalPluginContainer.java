package com.darwinreforged.servermodifications.objects;

import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

public class InternalPluginContainer implements PluginContainer {

    private String id;
    private String version;
    private String name;

    public InternalPluginContainer(ModuleInfo plugin) {
        this.id = plugin.id();
        this.version = plugin.version();
        this.name = plugin.version();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getVersion() {
        return version != null ? Optional.of(version) : Optional.empty();
    }
}
