/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.data.DefaultObjectMapper;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.JsonInclusionRule;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

@Component
public class JacksonObjectMapper extends DefaultObjectMapper {

    private Include include = Include.ALWAYS;
    protected ObjectMapper objectMapper;

    @Inject
    private ApplicationContext context;

    public JacksonObjectMapper() {
        super(FileFormats.JSON);
    }

    @Override
    public <T> Result<T> read(final String content, final TypeContext<T> type) {
        return super.read(content, type);
    }

    @Override
    public <T> Result<T> read(final String content, final Class<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.getName());
        return Result.of(() -> this.configureMapper().readValue(content, type));
    }

    @Override
    public <T> Result<T> read(final Path path, final Class<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.getName());
        return Result.of(() -> this.configureMapper().readValue(path.toFile(), type));
    }

    @Override
    public <T> Result<T> read(final URL url, final Class<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.getName());
        return Result.of(() -> this.configureMapper().readValue(url, type));
    }

    @Override
    public <T> Result<T> read(final String content, final GenericType<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.type().getTypeName());
        return Result.of(() -> this.configureMapper().readValue(content, new GenericTypeReference<>(type)));
    }

    @Override
    public <T> Result<T> read(final Path path, final GenericType<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.type().getTypeName());
        return Result.of(() -> this.configureMapper().readValue(path.toFile(), new GenericTypeReference<>(type)));
    }

    @Override
    public <T> Result<T> read(final URL url, final GenericType<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.type().getTypeName());
        return Result.of(() -> this.configureMapper().readValue(url, new GenericTypeReference<>(type)));
    }

    @Override
    public <T> Result<Boolean> write(final Path path, final T content) {
        this.context.log().debug("Writing content of type " + TypeContext.of(content).name() + " to path " + path);
        if (content instanceof String string) return this.writePlain(path, string);
        return Result.of(() -> {
            this.writer(content).writeValue(path.toFile(), content);
            return true;
        }).orElse(() -> false);
    }

    @Override
    public <T> Result<String> write(final T content) {
        this.context.log().debug("Writing content of type " + TypeContext.of(content).name() + " to string value");
        return Result.of(() -> this.writer(content).writeValueAsString(content))
                .map(out -> out.replace("\\r", ""));
    }

    protected Result<Boolean> writePlain(final Path path, final String content) {
        try (final FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
            writer.flush();
            return Result.of(true);
        }
        catch (final IOException e) {
            return Result.of(false, e);
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
        final Map<String, Object> flat = new HashMap<>();
        try {
            final JsonNode jsonNode = node.get();
            this.addKeys("", jsonNode, flat);
        }
        catch (final FileNotFoundException e) {
            this.context.log().warn("File not found: " + e.getMessage());
        }
        catch (final IOException e) {
            this.context.handle(e);
        }
        return flat;
    }

    private ObjectWriter writer(final Object content) {
        ObjectWriter writer = this.configureMapper().writerWithDefaultPrettyPrinter();
        final Result<Component> annotated = TypeContext.of(content).annotation(Component.class);

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

    public ObjectMapper configureMapper() {
        if (null == this.objectMapper) {
            this.context.log().debug("Internal object mapper was not configured yet, configuring now with filetype " + this.fileType());
            final MapperBuilder<?, ?> builder = this.mapper(this.fileType());
            builder.annotationIntrospector(new PropertyAliasIntrospector());
            builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            builder.enable(Feature.ALLOW_COMMENTS);
            builder.enable(Feature.ALLOW_YAML_COMMENTS);
            builder.enable(SerializationFeature.INDENT_OUTPUT);
            builder.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // Hartshorn convention uses fluent style getters/setters, these are not picked up by Jackson
            // which would otherwise cause it to fail due to it recognizing the object as an empty bean,
            // even if it is not empty.
            builder.visibility(PropertyAccessor.FIELD, Visibility.ANY);
            builder.serializationInclusion(this.include);
            this.objectMapper = builder.build();
        }
        return this.objectMapper;
    }

    protected MapperBuilder<?, ?> mapper(final FileFormat fileFormat) {
        final JacksonDataMapper dataMapper = this.context.get(Key.of(JacksonDataMapper.class, fileFormat.extension()));
        // Do not throw an exception here as subclasses may wish to extend functionality
        return dataMapper == null ? null : dataMapper.get();
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
}
