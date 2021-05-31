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
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import org.dockbox.selene.persistence.GenericType;
import org.dockbox.selene.persistence.PersistentCapable;
import org.dockbox.selene.util.Reflect;

import java.lang.reflect.AnnotatedElement;
import java.nio.file.Path;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;

public abstract class DefaultJacksonManager extends DefaultAbstractFileManager {

    @AllArgsConstructor
    private enum Mappers {
        JSON(FileType.JSON, ObjectMapper::new),
        YAML(FileType.YAML, YAMLMapper::new),
        PROPERTIES(FileType.PROPERTIES, JavaPropsMapper::new),
        TOML(FileType.TOML, TomlMapper::new),
        XML(FileType.XML, XmlMapper::new),
        ;

        private final FileType fileType;
        private final Supplier<? super ObjectMapper> mapper;
    }

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
    public <T> Exceptional<T> read(Path file, GenericType<T> type) {
        if (type.getType() instanceof Class) {
            //noinspection unchecked
            Exceptional<T> persistentCapable = this.correctPersistentCapable(file, (Class<T>) type.getType());
            if (persistentCapable.present()) return persistentCapable;
        }

        if (type.getType() instanceof AnnotatedElement) Reflect.serializable((AnnotatedElement) type.getType(), FileManager.class, true);

        return Exceptional.of(() -> this.configureMapper().readValue(file.toFile(), type));
    }

    @Override
    public <T> Exceptional<Boolean> write(Path file, T content) {
        if (content instanceof PersistentCapable) return this.write(file, ((PersistentCapable<?>) content).toPersistentModel());

        Reflect.serializable(content.getClass(), FileManager.class, true);

        return Exceptional.of(() -> {
            this.configureMapper().writerWithDefaultPrettyPrinter().writeValue(file.toFile(), content);
            return true;
        }).then(() -> false);
    }

    protected ObjectMapper configureMapper() {
        if (null == mapper) {
            mapper = this.getMapper(this.getFileType());
            mapper.setAnnotationIntrospector(new PropertyAliasIntrospector());
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.configure(Feature.ALLOW_COMMENTS, true);
            mapper.configure(Feature.ALLOW_YAML_COMMENTS, true);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
        }
        return mapper;
    }

    protected ObjectMapper getMapper(FileType fileType) {
        for (Mappers mapper : Mappers.values()) {
            if (mapper.fileType.equals(fileType)) return (ObjectMapper) mapper.mapper.get();
        }
        return null; // Do not throw an exception here as subclasses may wish to extend functionality
    }
}
