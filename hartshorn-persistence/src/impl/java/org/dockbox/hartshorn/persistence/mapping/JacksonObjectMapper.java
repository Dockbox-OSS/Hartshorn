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

package org.dockbox.hartshorn.persistence.mapping;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.annotations.Entity;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.PersistentCapable;
import org.dockbox.hartshorn.persistence.PersistentModel;
import org.dockbox.hartshorn.persistence.jackson.GenericTypeReference;
import org.dockbox.hartshorn.persistence.jackson.PropertyAliasIntrospector;
import org.dockbox.hartshorn.persistence.properties.PersistenceModifier;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@Binds(org.dockbox.hartshorn.persistence.mapping.ObjectMapper.class)
public class JacksonObjectMapper extends DefaultObjectMapper {

    private Include include = Include.ALWAYS;

    @AllArgsConstructor
    private enum Mappers {
        JSON(FileType.JSON, ObjectMapper::new),
        YAML(FileType.YAML, () -> {
            final YAMLFactory yamlFactory = new YAMLFactory();
            yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
            yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            yamlFactory.disable(YAMLParser.Feature.EMPTY_STRING_AS_NULL);
            return new YAMLMapper(yamlFactory);
        }),
        PROPERTIES(FileType.PROPERTIES, JavaPropsMapper::new),
        TOML(FileType.TOML, TomlMapper::new),
        XML(FileType.XML, XmlMapper::new),
        ;

        private final FileType fileType;
        private final Supplier<? super ObjectMapper> mapper;
    }

    private static final List<FileType> roots = Collections.singletonList(FileType.XML);

    protected ObjectMapper mapper;

    public JacksonObjectMapper() {
        super(FileType.JSON);
    }

    @Override
    public <T> Exceptional<T> read(String content, Class<T> type) {
        return this.readInternal(
                type,
                () -> this.correctPersistentCapable(content, type),
                () -> this.configureMapper().readValue(content, type));
    }

    @Override
    public <T> Exceptional<T> read(Path path, Class<T> type) {
        return this.readInternal(
                type,
                () -> this.correctPersistentCapable(path, type),
                () -> this.configureMapper().readValue(path.toFile(), type));
    }

    @Override
    public <T> Exceptional<T> read(String content, GenericType<T> type) {
        return this.readInternal(
                type.getType(),
                () -> this.correctPersistentCapable(content, (Class<T>) type.getType()),
                () -> this.configureMapper().readValue(content, new GenericTypeReference<>(type))
        );
    }

    @Override
    public <T> Exceptional<T> read(Path path, GenericType<T> type) {
        return this.readInternal(
                type.getType(),
                () -> this.correctPersistentCapable(path, (Class<T>) type.getType()),
                () -> this.configureMapper().readValue(path.toFile(), new GenericTypeReference<>(type))
                );
    }

    private <T> Exceptional<T> readInternal(Type type, Supplier<Exceptional<T>> capable, Callable<T> reader) {
        if (type instanceof Class) {
            Exceptional<T> persistentCapable = capable.get();
            if (persistentCapable.present()) return persistentCapable;
        }

        if (type instanceof AnnotatedElement) Reflect.serializable((AnnotatedElement) type, FileManager.class, true);

        return Exceptional.of(reader);
    }

    @Override
    public <T> Exceptional<Boolean> write(Path path, T content) {
        return this.writeInternal(
                content,
                () -> this.write(path, ((PersistentCapable<?>) content).model()),
                () -> {
                    this.writer(content).writeValue(path.toFile(), content);
                    return true;
                }).orElse(() -> false);
    }

    @Override
    public <T> Exceptional<String> write(T content) {
        return this.writeInternal(
                content,
                () -> this.write(((PersistentCapable<?>) content).model()),
                () -> this.writer(content).writeValueAsString(content))
                .map(out -> out.replaceAll("\\r", ""));
    }

    private ObjectWriter writer(Object content) {
        ObjectWriter writer = this.configureMapper().writerWithDefaultPrettyPrinter();
        final Exceptional<Entity> annotated = Reflect.annotation(content.getClass(), Entity.class);

        if (JacksonObjectMapper.roots.contains(this.fileType()) && annotated.present()) {
            final Entity annotation = annotated.get();
            writer = writer.withRootName(annotation.value());
        }
        return writer;
    }

    private <T, R> Exceptional<R> writeInternal(T content, Supplier<Exceptional<R>> capable, Callable<R> writer) {
        if (content instanceof PersistentCapable) return capable.get();
        Reflect.serializable(content.getClass(), FileManager.class, true);
        return Exceptional.of(writer);
    }

    @Override
    public void fileType(FileType fileType) {
        super.fileType(fileType);
        this.mapper = null;
    }

    @Override
    protected void modify(PersistenceModifier modifier) {
        this.include = switch (modifier) {
            case SKIP_EMPTY -> Include.NON_EMPTY;
            case SKIP_NULL -> Include.NON_NULL;
        };
    }

    protected ObjectMapper configureMapper() {
        if (null == this.mapper) {
            this.mapper = this.mapper(this.fileType());
            this.mapper.setAnnotationIntrospector(new PropertyAliasIntrospector());
            this.mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            this.mapper.enable(Feature.ALLOW_COMMENTS);
            this.mapper.enable(Feature.ALLOW_YAML_COMMENTS);
            this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
            this.mapper.enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
            this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            this.mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
            this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // As Lombok generates fluent style getters/setters, these are not picked up by Jackson which
            // would otherwise cause it to fail due to it recognizing the object as an empty bean, even
            // if it is not empty.
            this.mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            this.mapper.setSerializationInclusion(this.include);
        }
        return this.mapper;
    }

    protected ObjectMapper mapper(FileType fileType) {
        for (JacksonObjectMapper.Mappers mapper : JacksonObjectMapper.Mappers.values()) {
            if (mapper.fileType.equals(fileType)) return (ObjectMapper) mapper.mapper.get();
        }
        return null; // Do not throw an exception here as subclasses may wish to extend functionality
    }

    protected <T> Exceptional<T> correctPersistentCapable(Path file, Class<T> type) {
        return this.correctPersistentCapableInternal(type, model -> this.read(file, model));
    }

    protected <T> Exceptional<T> correctPersistentCapable(String content, Class<T> type) {
        return this.correctPersistentCapableInternal(type, model -> this.read(content, model));
    }

    private <T, I> Exceptional<T> correctPersistentCapableInternal(Class<T> type, Function<Class<? extends PersistentModel<?>>, Exceptional<? extends PersistentModel<?>>> reader) {
        if (Reflect.assigns(PersistentCapable.class, type)) {
            // Provision basis is required here, as injected types will typically pass in a interface type. If no injection point is available a
            // regular instance is created through available constructors.
            Class<? extends PersistentModel<?>> modelType = ((PersistentCapable<?>) Hartshorn.context().get(type)).type();
            @NotNull Exceptional<? extends PersistentModel<?>> model = reader.apply(modelType);
            return model.map(PersistentModel::restore).map(out -> (T) out);
        }
        return Exceptional.empty();
    }
}
