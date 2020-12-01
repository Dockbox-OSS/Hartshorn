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

package org.dockbox.selene.core.impl.files;

import com.google.common.reflect.TypeToken;

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.files.FileType;
import org.dockbox.selene.core.impl.files.mapping.NeutrinoObjectMapper;
import org.dockbox.selene.core.impl.files.mapping.NeutrinoObjectMapperFactory;
import org.dockbox.selene.core.impl.files.serialize.SeleneTypeSerializers;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.files.ConfigurateManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import ninja.leaping.configurate.xml.XMLConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

/**
 Implementation of {@link ConfigurateManager} containing dynamic mapping to appropriate {@link ConfigurationLoader}s
 based on the given {@link FileType} by the implementation of this type. Supporting {@link FileType#YAML},
 {@link FileType#JSON}, {@link FileType#XML}, {@link FileType#MOD_CONFIG}, and {@link FileType#CONFIG} file types.

 Supports type mapping through {@link NeutrinoObjectMapper}, which allows for additional functionality on top of the
 normal Configurate functionality. See <a href="https://github.com/NucleusPowered/Neutrino">Neutrino</a> for reference.

 Automatically generates, and checks the presence of, files in their directories. For both custom file locations and
 {@link Extension#id()} based.
 */
public abstract class DefaultConfigurateManager extends ConfigurateManager {

    /**
     Provides the given {@link FileType} to the super type {@link ConfigurateManager}. And registers any custom
     {@link ninja.leaping.configurate.objectmapping.serialize.TypeSerializer} types to
     {@link TypeSerializers#getDefaultSerializers()}.

     @param fileType
     The file type to be used when mapping.
     */
    protected DefaultConfigurateManager(FileType fileType) {
        super(fileType);
        SeleneTypeSerializers.registerTypeSerializers();
    }

    private final ConfigurationLoader<?> getConfigurationLoader(Path file) throws UnsupportedFileException {
        switch (this.getFileType()) {
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
                throw new UnsupportedFileException(this.getFileType().getExtension());
        }
    }

    @NotNull
    @Override
    public <T> Exceptional<T> getFileContent(@NotNull Path file, @NotNull Class<T> type) {
        try {
            final ConfigurationLoader<?> loader = this.getConfigurationLoader(file);
            final ConfigurationNode node = loader.load();
            this.verifyConfigurateType(type, node);

            final NeutrinoObjectMapper<T> mapper = NeutrinoObjectMapperFactory.builder()
                    .build(true)
                    .getMapper(type);

            final T content = mapper.bindToNew().populate(node);

            if (SeleneUtils.isFileEmpty(file)) {
                this.writeFileContent(file, content);
            }

            return Exceptional.ofNullable(content);
        } catch (IOException | IllegalArgumentException | ObjectMappingException | UnsupportedFileException e) {
            return Exceptional.of(e);
        }
    }

    @NotNull
    @Override
    public <T> Exceptional<Boolean> writeFileContent(@NotNull Path file, @NotNull T content) {
        try {

            final ConfigurationLoader<?> loader = this.getConfigurationLoader(file);
            final ConfigurationNode node = loader.load();
            this.verifyConfigurateType(content.getClass(), node);

            @SuppressWarnings("unchecked") final NeutrinoObjectMapper<T> mapper = NeutrinoObjectMapperFactory.builder()
                    .build(true)
                    .getMapper((Class<T>) content.getClass());

            mapper.bind(content).serialize(node);
            loader.save(node);

            return Exceptional.of(true);
        } catch (UnsupportedFileException | IOException | ObjectMappingException e) {
            return Exceptional.of(false, e);
        }
    }

    private final <T> void verifyConfigurateType(Class<T> type, ConfigurationNode node) throws IllegalArgumentException {
        if (type.isAnnotationPresent(ConfigSerializable.class)) return; // Valid

        TypeSerializer<T> serializer = node.getOptions().getSerializers().get(TypeToken.of(type));
        if (serializer != null) return; // Valid

        throw new IllegalArgumentException(
                "Configuration type [" + type.getCanonicalName() + "] should be annotated with " +
                        "ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable" +
                " or have a valid ninja.leaping.configurate.objectmapping.serialize.TypeSerializer " +
                " registered for it.");
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
        return this.createFileIfNotExists(
                this.getFileType().asPath(
                        this.getDataDir().resolve(extension.id()),
                        file
                )
        );
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Extension extension, @NotNull String file) {
        return this.createFileIfNotExists(
                this.getFileType().asPath(
                        this.getExtensionConfigsDir().resolve(extension.id()),
                        file
                )
        );
    }

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull Path path) {
        return SeleneUtils.createPathIfNotExists(path);
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull Path file) {
        return SeleneUtils.createFileIfNotExists(file);
    }

}
