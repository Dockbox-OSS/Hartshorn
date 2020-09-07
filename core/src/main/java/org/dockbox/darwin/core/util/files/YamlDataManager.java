/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}. 
 */

package org.dockbox.darwin.core.util.files;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.server.ServerReference;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class YamlDataManager extends ServerReference implements DataManager {

    private final ObjectMapper mapper;

    public YamlDataManager() {
        YAMLFactory fact = new YAMLFactory();
        fact.disable(Feature.WRITE_DOC_START_MARKER);
        mapper = new ObjectMapper(fact);

        mapper.findAndRegisterModules();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Class<?> module) {
        return runWithExtension(module, (annotation) -> getInstance(FileUtils.class).getDataDir().resolve(annotation.id()));
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDataDir((Class<?>) module);
    }

    @NotNull
    @Override
    public Path getDefaultDataFile(@NotNull Class<?> module) {
        return runWithExtension(module, (annotation) -> {
            Path dataPath = getDataDir(module);
            return getInstance(FileUtils.class).createFileIfNotExists(dataPath.resolve(annotation.id() + ".yml"));
        });
    }

    @NotNull
    @Override
    public Path getDefaultDataFile(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDefaultDataFile((Class<?>) module);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    @Override
    public Map<String, Object> getDefaultDataFileContents(@NotNull Class<?> module) {
        return getDefaultDataFileContents(module, Map.class, new HashMap());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    @Override
    public Map<String, Object> getDefaultDataFileContents(@NotNull Object module) {
        return getDefaultDataFileContents(module, Map.class, new HashMap());
    }

    @NotNull
    @Override
    public <T> T getDefaultDataFileContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return runWithExtension(module, (annotation) -> {
            try {
                Path cf = getDefaultDataFile(module);
                T res = mapper.readValue(cf.toFile(), convertTo);
                return res != null ? res : defaultValue;
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map data contents", e);
            }
            return defaultValue;
        });
    }

    @NotNull
    @Override
    public <T> T getDefaultDataFileContents(@NotNull Object module, @NotNull Class<T> convertTo, T defaultValue) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDefaultDataFileContents((Class<?>) module, convertTo, defaultValue);
    }

    @Override
    public <T> void writeToDefaultDataFile(@NotNull Class<?> module, T data) {
        try {
            Path df = getDefaultDataFile(module);
            mapper.writeValue(df.toFile(), data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write data contents", e);
        }
    }

    @Override
    public <T> void writeToDefaultDataFile(@NotNull Object module, T data) {
        if (!(module instanceof Class)) module = module.getClass();
        writeToDefaultDataFile((Class<?>) module, data);
    }

    @Override
    public <T> void writeToDataFile(@NotNull Class<?> module, T data, @NotNull String fileName) {
        try {
            Path dataDir = getDataDir(module);
            Path df = getInstance(FileUtils.class).createFileIfNotExists(dataDir.resolve(fileName + ".yml"));
            mapper.writeValue(df.toFile(), data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write data contents", e);
        }
    }

    @Override
    public <T> void writeToDataFile(@NotNull Object module, T data, @NotNull String fileName) {
        if (!(module instanceof Class)) module = module.getClass();
        writeToDataFile((Class<?>) module, data, fileName);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Map<String, Object> getDataFileContents(@NotNull Class<?> module, @NotNull String fileName) {
        return runWithExtension(module, (annotation) -> {
            try {
                Path cf = getInstance(FileUtils.class).createFileIfNotExists(getDataDir(module).resolve(fileName + ".yml"));
                Map<String, Object> res = mapper.readValue(cf.toFile(), Map.class);
                return res != null ? res : new HashMap<>();
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map data contents", e);
            }
            return new HashMap<>();
        });
    }

    @NotNull
    @Override
    public Map<String, Object> getDataFileContents(@NotNull Object module, @NotNull String fileName) {
        return getDataFileContents(module.getClass(), fileName);
    }
}
