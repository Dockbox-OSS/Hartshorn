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

package org.dockbox.selene.persistence.jackson;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.persistence.DefaultAbstractFileManager;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileType;
import org.dockbox.selene.persistence.PersistentCapable;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public abstract class DefaultJacksonManager extends DefaultAbstractFileManager {

    private enum Mappers {
        JSON(FileType.JSON, ObjectMapper::new),
        YAML(FileType.YAML, YAMLMapper::new),
        PROPERTIES(FileType.PROPERTIES, JavaPropsMapper::new),
        TOML(FileType.TOML, TomlMapper::new),
        XML(FileType.XML, XmlMapper::new),
        ;

        Mappers(FileType fileType, Supplier<? super ObjectMapper> mapper) {
            mappers.put(fileType, mapper);
        }
    }

    protected static final Map<FileType, Supplier<? super ObjectMapper>> mappers = SeleneUtils.emptyConcurrentMap();
    protected static ObjectMapper mapper;

    protected DefaultJacksonManager() {
        super(FileType.JSON);
    }

    @Override
    public <T> Exceptional<T> read(Path file, Class<T> type) {
        Exceptional<T> persistentCapable = this.correctPersistentCapable(file, type);
        if (persistentCapable.present()) return persistentCapable;

        Reflect.serializable(type, FileManager.class, true);

        return Exceptional.of(() -> this.configureMapper().readValue(file.toFile(), type));
    }

    @Override
    public <T> Exceptional<Boolean> write(Path file, T content) {
        if (content instanceof PersistentCapable) return this.write(file, ((PersistentCapable<?>) content).toPersistentModel());

        Reflect.serializable(content.getClass(), FileManager.class, true);

        return Exceptional.of(() -> {
            this.configureMapper().writeValue(file.toFile(), content);
            return true;
        }).then(() -> false);
    }

    protected ObjectMapper configureMapper() {
        if (null == mapper) {
            mapper = (ObjectMapper) mappers.get(this.getFileType()).get();
            mapper.setAnnotationIntrospector(new PropertyAliasIntrospector());
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.configure(Feature.ALLOW_COMMENTS, true);
            mapper.configure(Feature.ALLOW_YAML_COMMENTS, true);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        return mapper;
    }
}
