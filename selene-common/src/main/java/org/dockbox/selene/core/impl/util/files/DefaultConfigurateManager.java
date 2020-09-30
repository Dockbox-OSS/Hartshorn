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

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.files.ConfigurateManager;
import org.dockbox.selene.core.util.files.FileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.xml.XMLConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public abstract class DefaultConfigurateManager extends ConfigurateManager {

    private final FileType fileType;

    protected DefaultConfigurateManager(FileType fileType) {
        this.fileType = fileType;
    }

    private final ConfigurationLoader<?> getConfigurationLoader(Path file) throws UnsupportedFileException {
        switch (this.fileType) {
            case YAML:
                return YAMLConfigurationLoader.builder().setPath(file).build();
            case JSON:
                return GsonConfigurationLoader.builder().setPath(file).build();
            case XML:
                return XMLConfigurationLoader.builder().setPath(file).build();
            case MOD_CONFIG:
            case CONFIG:
                return HoconConfigurationLoader.builder().setPath(file).build();
            default:
                throw new UnsupportedFileException(this.fileType.getExtension());
        }
    }

    @NotNull
    @Override
    public <T> Exceptional<T> getFileContent(@NotNull Path file, @NotNull Class<T> type) {
        try {
            this.verifyConfigurateType(type);

            final ConfigurationLoader<?> loader = this.getConfigurationLoader(file);
            final ConfigurationNode node = loader.load();
            final ObjectMapper<T> mapper = ObjectMapper.forClass(type);

            final T content = mapper.bindToNew().populate(node);

            return Exceptional.ofNullable(content);
        } catch (IOException | IllegalArgumentException | ObjectMappingException | UnsupportedFileException e) {
            return Exceptional.of(null, e);
        }
    }

    @NotNull
    @Override
    public <T> Exceptional<Boolean> writeFileContent(@NotNull Path file, @NotNull T content) {
        try {
            this.verifyConfigurateType(content.getClass());

            final ConfigurationLoader<?> loader = this.getConfigurationLoader(file);
            final ConfigurationNode node = loader.load();
            @SuppressWarnings("unchecked")
            final ObjectMapper<T> mapper = ObjectMapper.forClass((Class<T>) content.getClass());

            mapper.bind(content).serialize(node);
            loader.save(node);
            return Exceptional.of(true);
        } catch (UnsupportedFileException | IOException | ObjectMappingException e) {
            return Exceptional.of(false, e);
        }
    }

    private final <T> void verifyConfigurateType(Class<T> type) throws IllegalArgumentException {
        if (!type.isAnnotationPresent(ConfigSerializable.class)) {
            throw new IllegalArgumentException(
                    "Configuration type [" + type.getCanonicalName() + "] should be annotated with " +
                            "ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable");
        }
    }

    @NotNull
    @Override
    public Path getDataFile(@NotNull Extension extension) {
        return this.getDataFile(extension, extension.id());
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Extension extension) {
        return this.getConfigFile(extension, extension.id());
    }

    @NotNull
    @Override
    public Path getDataFile(@NotNull Extension extension, @NotNull String file) {
        return this.createPathIfNotExists(
                this.fileType.asPath(
                        this.getDataDir().resolve(extension.id()),
                        file
                )
        );
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Extension extension, @NotNull String file) {
        return this.createPathIfNotExists(
                this.fileType.asPath(
                        this.getExtensionConfigsDir().resolve(extension.id()),
                        file
                )
        );
    }

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            } catch (IOException ex) {
                Selene.getServer().except("Could not create file '" + file.getFileName() + "'", ex);
            }
        }
        return file;
    }

}
