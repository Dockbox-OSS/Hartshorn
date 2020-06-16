package org.dockbox.darwin.core.util.files;

import com.j256.ormlite.dao.Dao;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class YamlSQLiteDataManager implements DataManager<Dao<?, ?>> {

    @NotNull
    @Override
    public Path getDataDir(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Object module) {
        return null;
    }

    @NotNull
    @Override
    public File getDefaultDataFile(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public File getDefaultDataFile(@NotNull Object module) {
        return null;
    }

    @NotNull
    @Override
    public File getDefaultBulkDataFile(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public File getDefaultBulkDataFile(@NotNull Object module) {
        return null;
    }

    @Override
    public Dao<?, ?> getBulkDao(@NotNull Class<?> module, @NotNull Class<?> type, @NotNull File file) {
        return null;
    }

    @Override
    public Dao<?, ?> getBulkDao(@NotNull Object module, @NotNull Class<?> type, @NotNull File file) {
        return null;
    }

    @Override
    public Dao<?, ?> getBulkDao(@NotNull Class<?> type, @NotNull File file) {
        return null;
    }

    @NotNull
    @Override
    public Map<String, Object> getDataContents(@NotNull Class<?> module) {
        return null;
    }

    @NotNull
    @Override
    public Map<String, Object> getDataContents(@NotNull Object module) {
        return null;
    }

    @Override
    public void writeToData(@NotNull Class<?> module, @NotNull Map<String, ?> data) {

    }

    @Override
    public void writeToData(@NotNull Object module, @NotNull Map<String, ?> data) {

    }

    @NotNull
    @Override
    public <T> T getDataContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return null;
    }

    @NotNull
    @Override
    public <T> T getDataContents(@NotNull Object module, @NotNull Class<T> convertTo, T defaultValue) {
        return null;
    }

    @Override
    public <T> void writeToData(@NotNull Class<?> module, T data) {

    }

    @Override
    public <T> void writeToData(@NotNull Object module, T data) {

    }
}
