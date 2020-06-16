package org.dockbox.darwin.core.util.files;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class YamlConfigManager implements ConfigManager {

    @NotNull
    @Override
    public Path getConfigDir(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public Path getConfigDir(@NotNull Object module) {
        return null;
    }

    @NotNull
    @Override
    public File getConfigFile(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public File getConfigFile(@NotNull Object module) {
        return null;
    }

    @NotNull
    @Override
    public Map<String, Object> getConfigContents(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public Map<String, Object> getConfigContents(@NotNull Object module) {
        return null;
    }

    @NotNull
    @Override
    public <T> T getConfigContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return null;
    }

    @NotNull
    @Override
    public <T> T getConfigContents(@NotNull Object module, @NotNull Class<T> convertTo, T defaultValue) {
        return null;
    }

    @Override
    public void writeToConfig(@NotNull Class<?> module, @NotNull Map<String, ?> data) {
    }

    @Override
    public void writeToConfig(@NotNull Object module, @NotNull Map<String, ?> data) {
    }

    @Override
    public <T> void writeToConfig(@NotNull Class<?> module, T data) {
    }

    @Override
    public <T> void writeToConfig(@NotNull Object module, T data) {

    }
}
