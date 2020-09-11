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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class YamlDataManager extends DataManager {

    private final ObjectMapper mapper;

    public YamlDataManager() {
        YAMLFactory fact = new YAMLFactory();
        fact.disable(Feature.WRITE_DOC_START_MARKER);
        this.mapper = new ObjectMapper(fact);

        this.mapper.findAndRegisterModules();
        this.mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        this.mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    }

    @NotNull
    @Override
    public <T> T getDefaultDataFileContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return this.runWithExtension(module, (annotation) -> {
            try {
                Path cf = this.getDefaultDataFile(module);
                T res = this.mapper.readValue(cf.toFile(), convertTo);
                return null != res ? res : defaultValue;
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map data contents", e);
            }
            return defaultValue;
        });
    }

    @Override
    public <T> void writeToDefaultDataFile(@NotNull Class<?> module, T data) {
        try {
            Path df = this.getDefaultDataFile(module);
            this.mapper.writeValue(df.toFile(), data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write data contents", e);
        }
    }

    @Override
    public <T> void writeToDataFile(@NotNull Class<?> module, T data, @NotNull String fileName) {
        try {
            Path dataDir = this.getDataDir(module);
            Path df = this.getInstance(FileUtils.class).createFileIfNotExists(this.getFileType().asPath(dataDir, fileName));
            this.mapper.writeValue(df.toFile(), data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write data contents", e);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Map<String, Object> getDataFileContents(@NotNull Class<?> module, @NotNull String fileName) {
        return this.runWithExtension(module, (annotation) -> {
            try {
                Path cf = this.getInstance(FileUtils.class).createFileIfNotExists(
                        this.getFileType().asPath(
                                this.getDataDir(module),
                                fileName
                        )
                );
                Map<String, Object> res = this.mapper.readValue(cf.toFile(), Map.class);
                return null != res ? res : new HashMap<>();
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map data contents", e);
            }
            return new HashMap<>();
        });
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return FileType.YAML;
    }
}
