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

package org.dockbox.hartshorn.persistence.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.core.GenericType;
import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.DefaultObjectMapper;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.PersistentCapable;
import org.dockbox.hartshorn.persistence.PersistentModel;
import org.dockbox.hartshorn.persistence.properties.PersistenceModifier;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;

import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@Binds(org.dockbox.hartshorn.persistence.mapping.ObjectMapper.class)
public class JacksonObjectMapper extends DefaultObjectMapper {

    private static final List<FileType> roots = Collections.singletonList(FileType.XML);
    protected ObjectMapper mapper;
    private Include include = Include.ALWAYS;

    @Inject
    private ApplicationContext context;

    public JacksonObjectMapper() {
        super(FileType.JSON);
    }

    @Override
    public <T> Exceptional<T> read(String content, TypeContext<T> type) {
        return super.read(content, type);
    }

    @Override
    public <T> Exceptional<T> read(final String content, final Class<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.getName());
        return this.readInternal(
                type,
                () -> this.correctPersistentCapable(content, type),
                () -> this.configureMapper().readValue(content, type));
    }

    @Override
    public <T> Exceptional<T> read(final Path path, final Class<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.getName());
        return this.readInternal(
                type,
                () -> this.correctPersistentCapable(path, type),
                () -> this.configureMapper().readValue(path.toFile(), type));
    }

    @Override
    public <T> Exceptional<T> read(final URL url, final Class<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.getName());
        return this.readInternal(
                type,
                () -> this.correctPersistentCapable(url, type),
                () -> this.configureMapper().readValue(url, type));
    }

    @Override
    public <T> Exceptional<T> read(final String content, final GenericType<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.type().getTypeName());
        return this.readInternal(
                type.type(),
                () -> this.correctPersistentCapable(content, (Class<T>) type.type()),
                () -> this.configureMapper().readValue(content, new GenericTypeReference<>(type))
        );
    }

    @Override
    public <T> Exceptional<T> read(final Path path, final GenericType<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.type().getTypeName());
        return this.readInternal(
                type.type(),
                () -> this.correctPersistentCapable(path, (Class<T>) type.type()),
                () -> this.configureMapper().readValue(path.toFile(), new GenericTypeReference<>(type))
        );
    }

    @Override
    public <T> Exceptional<T> read(final URL url, final GenericType<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.type().getTypeName());
        return this.readInternal(
                type.type(),
                () -> this.correctPersistentCapable(url, (Class<T>) type.type()),
                () -> this.configureMapper().readValue(url, new GenericTypeReference<>(type))
        );
    }

    @Override
    public <T> Exceptional<Boolean> write(final Path path, final T content) {
        this.context.log().debug("Writing content of type " + TypeContext.of(content).name() + " to path " + path);
        return this.writeInternal(
                content,
                () -> this.write(path, ((PersistentCapable<?>) content).model()),
                () -> {
                    this.writer(content).writeValue(path.toFile(), content);
                    return true;
                }).orElse(() -> false);
    }

    @Override
    public <T> Exceptional<String> write(final T content) {
        this.context.log().debug("Writing content of type " + TypeContext.of(content).name() + " to string value");
        return this.writeInternal(
                        content,
                        () -> this.write(((PersistentCapable<?>) content).model()),
                        () -> this.writer(content).writeValueAsString(content))
                .map(out -> out.replaceAll("\\r", ""));
    }

    @Override
    public Map<String, Object> flat(String content) {
        this.context.log().debug("Reading content from string value to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(content));
    }

    @Override
    public Map<String, Object> flat(Path path) {
        this.context.log().debug("Reading content from path " + path + " to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(path.toFile()));
    }

    @Override
    public Map<String, Object> flat(URL url) {
        this.context.log().debug("Reading content from url " + url + " to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(url));
    }

    private Map<String, Object> flatInternal(FlatNodeSupplier node) {
        Map<String, Object> flat = HartshornUtils.emptyMap();
        try {
            JsonNode jsonNode = node.get();
            this.addKeys("", jsonNode, flat);
            return flat;
        } catch (IOException e) {
            Except.handle(e);
            return flat;
        }
    }

    private <T> Exceptional<T> readInternal(final Type type, final Supplier<Exceptional<T>> capable, final Callable<T> reader) {
        if (type instanceof Class) {
            final Exceptional<T> persistentCapable = capable.get();
            if (persistentCapable.present()) return persistentCapable;
        }
        return Exceptional.of(reader);
    }

    private ObjectWriter writer(final Object content) {
        ObjectWriter writer = this.configureMapper().writerWithDefaultPrettyPrinter();
        final Exceptional<Component> annotated = TypeContext.of(content).annotation(Component.class);

        if (JacksonObjectMapper.roots.contains(this.fileType()) && annotated.present()) {
            final Component annotation = annotated.get();
            writer = writer.withRootName(annotation.id());
        }
        return writer;
    }

    private <T, R> Exceptional<R> writeInternal(final T content, final Supplier<Exceptional<R>> capable, final Callable<R> writer) {
        if (content instanceof PersistentCapable) return capable.get();
        return Exceptional.of(writer);
    }

    @Override
    public JacksonObjectMapper fileType(final FileType fileType) {
        super.fileType(fileType);
        this.mapper = null;
        return this;
    }

    @Override
    protected void modify(final PersistenceModifier modifier) {
        this.include = switch (modifier) {
            case SKIP_EMPTY -> Include.NON_EMPTY;
            case SKIP_NULL -> Include.NON_NULL;
            default -> throw new IllegalArgumentException("Unknown modifier: " + modifier);
        };
    }

    protected ObjectMapper configureMapper() {
        if (null == this.mapper) {
            this.context.log().debug("Internal object mapper was not configured yet, configuring now with filetype " + this.fileType());
            this.mapper = this.mapper(this.fileType());
            this.mapper.setAnnotationIntrospector(new PropertyAliasIntrospector(this.context));
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

    protected ObjectMapper mapper(final FileType fileType) {
        for (final JacksonObjectMapper.Mappers mapper : JacksonObjectMapper.Mappers.values()) {
            if (mapper.fileType.equals(fileType)) return (ObjectMapper) mapper.mapper.get();
        }
        return null; // Do not throw an exception here as subclasses may wish to extend functionality
    }

    protected <T> Exceptional<T> correctPersistentCapable(final Path file, final Class<T> type) {
        return this.correctPersistentCapableInternal(type, model -> this.read(file, model));
    }

    protected <T> Exceptional<T> correctPersistentCapable(final String content, final Class<T> type) {
        return this.correctPersistentCapableInternal(type, model -> this.read(content, model));
    }

    protected <T> Exceptional<T> correctPersistentCapable(final URL url, final Class<T> type) {
        return this.correctPersistentCapableInternal(type, model -> this.read(url, model));
    }

    private <T> Exceptional<T> correctPersistentCapableInternal(final Class<T> type, final Function<Class<? extends PersistentModel<?>>, Exceptional<? extends PersistentModel<?>>> reader) {
        if (TypeContext.of(type).childOf(PersistentCapable.class)) {
            // Provision basis is required here, as injected types will typically pass in a interface type. If no injection point is available a
            // regular instance is created through available constructors.
            final Class<? extends PersistentModel<?>> modelType = ((PersistentCapable<?>) this.context.get(type)).type();
            @NotNull final Exceptional<? extends PersistentModel<?>> model = reader.apply(modelType);
            return model.map(m -> m.restore(this.context)).map(out -> (T) out);
        }
        return Exceptional.empty();
    }

    private void addKeys(String currentPath, TreeNode jsonNode, Map<String, Object> map) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                this.addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                this.addKeys(currentPath + "[" + i + "]", arrayNode.get(i), map);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, this.configureMapper().convertValue(valueNode, Object.class));
        }
    }

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
}
