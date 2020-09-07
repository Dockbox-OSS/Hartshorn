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

public class YamlConfigManager extends ServerReference implements ConfigManager {

    private final ObjectMapper mapper;

    public YamlConfigManager() {
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
    public Path getConfigDir(@NotNull Class<?> module) {
        return runWithExtension(module, (annotation) -> Server.getInstance(FileUtils.class).getModuleConfigDir().resolve(annotation.id()));
    }

    @NotNull
    @Override
    public Path getConfigDir(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getConfigDir((Class<?>) module);
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Class<?> module) {
        return runWithExtension(module, (annotation) -> {
            Path configPath = getConfigDir(module);
            return Server.getInstance(FileUtils.class).createFileIfNotExists(configPath.resolve(annotation.id() + ".yml"));
        });
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getConfigFile((Class<?>) module);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    @Override
    public Map<String, Object> getConfigContents(@NotNull Class<?> module) {
        return getConfigContents(module, Map.class, new HashMap());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    @Override
    public Map<String, Object> getConfigContents(@NotNull Object module) {
        return getConfigContents(module, Map.class, new HashMap());
    }

    @NotNull
    @Override
    public <T> T getConfigContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return runWithExtension(module, (annotation) -> {
            try {
                Path cf = getConfigFile(module);
                T res = mapper.readValue(cf.toFile(), convertTo);
                return res != null ? res : defaultValue;
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map config contents", e);
            }
            return defaultValue;
        });
    }

    @NotNull
    @Override
    public <T> T getConfigContents(@NotNull Object module, @NotNull Class<T> convertTo, T defaultValue) {
        if (!(module instanceof Class)) module = module.getClass();
        return getConfigContents((Class<?>) module, convertTo, defaultValue);
    }

    @Override
    public <T> void writeToConfig(@NotNull Class<?> module, T data) {
        try {
            Path cf = getConfigFile(module);
            mapper.writeValue(cf.toFile(), data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write config contents", e);
        }
    }

    @Override
    public <T> void writeToConfig(@NotNull Object module, T data) {
        if (!(module instanceof Class)) module = module.getClass();
        writeToConfig((Class<?>) module, data);
    }
}
