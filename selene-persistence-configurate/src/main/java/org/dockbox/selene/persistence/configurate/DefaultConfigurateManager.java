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

package org.dockbox.selene.persistence.configurate;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.api.entity.annotations.Metadata;
import org.dockbox.selene.persistence.DefaultAbstractFileManager;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileType;
import org.dockbox.selene.persistence.PersistentCapable;
import org.dockbox.selene.persistence.UnsupportedFileException;
import org.dockbox.selene.persistence.configurate.serialize.SeleneTypeSerializers;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.xml.XmlConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.nio.file.Path;

import io.leangen.geantyref.GenericTypeReflector;

/**
 * Implementation of {@link FileManager} containing dynamic mapping to appropriate {@link
 * ConfigurationLoader}s based on the given {@link FileType} by the implementation of this type.
 * Supporting {@link FileType#YAML}, {@link FileType#JSON}, {@link FileType#XML}, {@link
 * FileType#MOD_CONFIG}, and {@link FileType#CONFIG} file types.
 *
 * <p>Automatically generates, and checks the presence of, files in their directories. For both
 * custom file locations and {@link TypedOwner#id()} based.
 */
public abstract class DefaultConfigurateManager extends DefaultAbstractFileManager {

    /**
     * Provides the given {@link FileType} to the super type {@link FileManager}.
     *
     * @param fileType
     *         The file type to be used when mapping.
     */
    protected DefaultConfigurateManager(FileType fileType) {
        super(fileType);
    }

    @NotNull
    @Override
    public <T> Exceptional<T> read(@NotNull Path file, @NotNull Class<T> type) {
        Exceptional<T> persistentCapableContent = this.correctPersistentCapable(file, type);
        if (persistentCapableContent.present()) return persistentCapableContent;

        Reflect.rejects(type, DefaultConfigurateManager.class, true);

        try {
            final ConfigurationLoader<?> loader = this.getConfigurationLoader(file);
            final ConfigurationNode node = loader.load();

            final ObjectMapper<T> mapper = ObjectMapper.factory().get(type);

            final T content = mapper.load(node);

            if (SeleneUtils.isFileEmpty(file)) {
                this.write(file, content);
            }

            return Exceptional.of(content);
        }
        catch (IOException | IllegalArgumentException | UnsupportedFileException e) {
            return Exceptional.of(e);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T> Exceptional<Boolean> write(@NotNull Path file, @NotNull T content) {
        if (content instanceof PersistentCapable) return this.write(file, ((PersistentCapable<?>) content).toPersistentModel());

        Reflect.rejects(content.getClass(), DefaultConfigurateManager.class, true);

        try {
            final ConfigurationLoader<?> loader = this.getConfigurationLoader(file);
            final ConfigurationNode node = loader.load();

            final ObjectMapper<T> mapper =
                    (ObjectMapper<T>) ObjectMapper.factory().get(content.getClass());

            mapper.save(content, node);
            loader.save(node);

            return Exceptional.of(true);
        }
        catch (UnsupportedFileException | IOException e) {
            return Exceptional.of(false, e);
        }
    }

    @Override
    public void requestFileType(FileType fileType) {
        switch (fileType) {
            case YAML:
            case JSON:
            case XML:
            case MOD_CONFIG:
            case CONFIG:
                super.setFileType(fileType);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Configurate does not support " + fileType.getExtension());
        }
    }

    private ConfigurationLoader<?> getConfigurationLoader(Path file) throws UnsupportedFileException {
        AbstractConfigurationLoader.Builder<?, ?> builder;
        switch (this.getFileType()) {
            case YAML:
                builder = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.FLOW);
                break;
            case JSON:
                builder = GsonConfigurationLoader.builder();
                break;
            case XML:
                builder = XmlConfigurationLoader.builder();
                break;
            case MOD_CONFIG:
            case CONFIG:
                builder = HoconConfigurationLoader.builder();
                break;
            default:
                throw new UnsupportedFileException(this.getFileType().getExtension());
        }
        return builder.path(file)
                .defaultOptions(opts -> opts.serializers(build -> build
                        .registerAll(SeleneTypeSerializers.collection())
                        .register(type -> {
                            AnnotatedType annotatedType = GenericTypeReflector.annotate(type);
                            return annotatedType.isAnnotationPresent(Metadata.class)
                                    && annotatedType.getAnnotation(Metadata.class).serializable();
                        }, ObjectMapper.factory().asTypeSerializer()))
                ).build();
    }
}
