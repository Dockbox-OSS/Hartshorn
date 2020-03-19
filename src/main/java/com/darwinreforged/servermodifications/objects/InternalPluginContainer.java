package com.darwinreforged.servermodifications.objects;

import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.plugin.meta.PluginDependency;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class InternalPluginContainer implements PluginContainer {

    private String id;
    private String version;
    private String name;

    public InternalPluginContainer(Plugin plugin) {
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
