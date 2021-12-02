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

package org.dockbox.hartshorn.data.jackson;

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
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
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

import org.dockbox.hartshorn.core.GenericType;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.DefaultObjectMapper;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.JsonInclusionRule;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.inject.Inject;

import lombok.AllArgsConstructor;

@Binds(org.dockbox.hartshorn.data.mapping.ObjectMapper.class)
public class JacksonObjectMapper extends DefaultObjectMapper {

    private Include include = Include.ALWAYS;
    protected ObjectMapper objectMapper;

    @Inject
    private ApplicationContext context;

    public JacksonObjectMapper() {
        super(FileFormats.JSON);
    }

    @Override
    public <T> Exceptional<T> read(final String content, final TypeContext<T> type) {
        return super.read(content, type);
    }

    @Override
    public <T> Exceptional<T> read(final String content, final Class<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.getName());
        return Exceptional.of(() -> this.configureMapper().readValue(content, type));
    }

    @Override
    public <T> Exceptional<T> read(final Path path, final Class<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.getName());
        return Exceptional.of(() -> this.configureMapper().readValue(path.toFile(), type));
    }

    @Override
    public <T> Exceptional<T> read(final URL url, final Class<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.getName());
        return Exceptional.of(() -> this.configureMapper().readValue(url, type));
    }

    @Override
    public <T> Exceptional<T> read(final String content, final GenericType<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.type().getTypeName());
        return Exceptional.of(() -> this.configureMapper().readValue(content, new GenericTypeReference<>(type)));
    }

    @Override
    public <T> Exceptional<T> read(final Path path, final GenericType<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.type().getTypeName());
        return Exceptional.of(() -> this.configureMapper().readValue(path.toFile(), new GenericTypeReference<>(type)));
    }

    @Override
    public <T> Exceptional<T> read(final URL url, final GenericType<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.type().getTypeName());
        return Exceptional.of(() -> this.configureMapper().readValue(url, new GenericTypeReference<>(type)));
    }

    @Override
    public <T> Exceptional<Boolean> write(final Path path, final T content) {
        this.context.log().debug("Writing content of type " + TypeContext.of(content).name() + " to path " + path);
        if (content instanceof String string) return this.writePlain(path, string);
        return Exceptional.of(() -> {
            this.writer(content).writeValue(path.toFile(), content);
            return true;
        }).orElse(() -> false);
    }

    @Override
    public <T> Exceptional<String> write(final T content) {
        this.context.log().debug("Writing content of type " + TypeContext.of(content).name() + " to string value");
        return Exceptional.of(() -> this.writer(content).writeValueAsString(content))
                .map(out -> out.replaceAll("\\r", ""));
    }

    protected Exceptional<Boolean> writePlain(final Path path, final String content) {
        try (final FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
            writer.flush();
            return Exceptional.of(true);
        }
        catch (final IOException e) {
            return Exceptional.of(false, e);
        }
    }

    @Override
    public Map<String, Object> flat(final String content) {
        this.context.log().debug("Reading content from string value to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(content));
    }

    @Override
    public Map<String, Object> flat(final Path path) {
        this.context.log().debug("Reading content from path " + path + " to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(path.toFile()));
    }

    @Override
    public Map<String, Object> flat(final URL url) {
        this.context.log().debug("Reading content from url " + url + " to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(url));
    }

    private Map<String, Object> flatInternal(final FlatNodeSupplier node) {
        final Map<String, Object> flat = HartshornUtils.emptyMap();
        try {
            final JsonNode jsonNode = node.get();
            this.addKeys("", jsonNode, flat);
            return flat;
        }
        catch (final IOException e) {
            this.context.handle(e);
            return flat;
        }
    }

    private ObjectWriter writer(final Object content) {
        ObjectWriter writer = this.configureMapper().writerWithDefaultPrettyPrinter();
        final Exceptional<Component> annotated = TypeContext.of(content).annotation(Component.class);

        // Currently, only XML supports changing the root name, if XML is used we can change the
        // root name to be equal to the ID of the component.
        if (this.fileType().equals(FileFormats.XML) && annotated.present()) {
            final Component annotation = annotated.get();
            writer = writer.withRootName(annotation.id());
        }
        return writer;
    }

    @Override
    public JacksonObjectMapper fileType(final FileFormat fileFormat) {
        super.fileType(fileFormat);
        this.objectMapper = null;
        return this;
    }

    @Override
    public JacksonObjectMapper skipBehavior(final JsonInclusionRule modifier) {
        this.include = switch (modifier) {
            case SKIP_EMPTY -> Include.NON_EMPTY;
            case SKIP_NULL -> Include.NON_NULL;
            case SKIP_DEFAULT -> Include.NON_DEFAULT;
            case SKIP_NONE -> Include.ALWAYS;
            default -> throw new IllegalArgumentException("Unknown modifier: " + modifier);
        };
        this.objectMapper = null;
        return this;
    }

    protected ObjectMapper configureMapper() {
        if (null == this.objectMapper) {
            this.context.log().debug("Internal object mapper was not configured yet, configuring now with filetype " + this.fileType());
            final MapperBuilder<?, ?> builder = this.mapper(this.fileType());
            builder.annotationIntrospector(new PropertyAliasIntrospector(this.context));
            builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            builder.enable(Feature.ALLOW_COMMENTS);
            builder.enable(Feature.ALLOW_YAML_COMMENTS);
            builder.enable(SerializationFeature.INDENT_OUTPUT);
            builder.enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
            builder.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            builder.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // As Lombok generates fluent style getters/setters, these are not picked up by Jackson which
            // would otherwise cause it to fail due to it recognizing the object as an empty bean, even
            // if it is not empty.
            builder.visibility(PropertyAccessor.FIELD, Visibility.ANY);
            builder.serializationInclusion(this.include);
            this.objectMapper = builder.build();
        }
        return this.objectMapper;
    }

    protected MapperBuilder<?, ?> mapper(final FileFormat fileFormat) {
        for (final JacksonObjectMapper.Mappers mapper : Mappers.VALUES) {
            if (mapper.fileFormat == fileFormat) return (MapperBuilder<?, ?>) mapper.mapper.get();
        }
        return null; // Do not throw an exception here as subclasses may wish to extend functionality
    }

    private void addKeys(final String currentPath, final TreeNode jsonNode, final Map<String, Object> map) {
        if (jsonNode.isObject()) {
            final ObjectNode objectNode = (ObjectNode) jsonNode;
            final Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
            final String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";

            while (iter.hasNext()) {
                final Map.Entry<String, JsonNode> entry = iter.next();
                this.addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        }
        else if (jsonNode.isArray()) {
            final ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                this.addKeys(currentPath + "[" + i + "]", arrayNode.get(i), map);
            }
        }
        else if (jsonNode.isValueNode()) {
            final ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, this.configureMapper().convertValue(valueNode, Object.class));
        }
    }

    @AllArgsConstructor
    private enum Mappers {
        JSON(FileFormats.JSON, JsonMapper::builder),
        YAML(FileFormats.YAML, () -> {
            final YAMLFactory yamlFactory = new YAMLFactory();
            yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
            yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            yamlFactory.disable(YAMLParser.Feature.EMPTY_STRING_AS_NULL);
            return YAMLMapper.builder(yamlFactory);
        }),
        PROPERTIES(FileFormats.PROPERTIES, JavaPropsMapper::builder),
        TOML(FileFormats.TOML, TomlMapper::builder),
        XML(FileFormats.XML, XmlMapper::builder),
        ;

        public static final Mappers[] VALUES = Mappers.values();
        private final FileFormats fileFormat;
        private final Supplier<? super MapperBuilder<?, ?>> mapper;
    }
}
