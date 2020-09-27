/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.util.files;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.files.ConfigManager;
import org.dockbox.selene.core.util.files.FileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public class YamlConfigManager extends ConfigManager {

    private final ObjectMapper mapper;

    public YamlConfigManager() {
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
    public <T> T getConfigContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return this.runWithExtension(module, (annotation) -> {
            try {
                Path cf = this.getConfigFile(module);
                T res = this.mapper.readValue(cf.toFile(), convertTo);
                return res != null ? res : defaultValue;
            } catch (IOException | IllegalArgumentException e) {
                Selene.getServer().except("Failed to map config contents", e);
            }
            return defaultValue;
        });
    }

    @Override
    public <T> void writeToConfig(@NotNull Class<?> module, T data) {
        try {
            Path cf = this.getConfigFile(module);
            this.mapper.writeValue(cf.toFile(), data);
        } catch (IOException e) {
            Selene.getServer().except("Failed to write config contents", e);
        }
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return FileType.YAML;
    }
}
