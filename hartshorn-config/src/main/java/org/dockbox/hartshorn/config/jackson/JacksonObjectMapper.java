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

package org.dockbox.hartshorn.config.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.config.DefaultObjectMapper;
import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.JsonInclusionRule;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.inject.Inject;

@Component
public class JacksonObjectMapper extends DefaultObjectMapper {

    protected ObjectMapper objectMapper;

    @Inject
    private ApplicationContext context;
    private JsonInclusionRule inclusionRule;

    public JacksonObjectMapper() {
        super(FileFormats.JSON);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final String content, final Class<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.getName());
        return Attempt.<T, JsonProcessingException>of(() -> this.configureMapper().readValue(content, type), JsonProcessingException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final Path path, final Class<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readValue(path.toFile(), type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final URL url, final Class<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readValue(url, type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final InputStream stream, final Class<T> type) {
        this.context.log().debug("Reading content from input stream to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readValue(stream, type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final String content, final GenericType<T> type) {
        this.context.log().debug("Reading content from string value to type " + type.type().getTypeName());
        return Attempt.of(() -> this.configureMapper().readValue(content, new GenericTypeReference<>(type)), JsonProcessingException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final Path path, final GenericType<T> type) {
        this.context.log().debug("Reading content from path " + path + " to type " + type.type().getTypeName());
        return Attempt.of(() -> this.configureMapper().readValue(path.toFile(), new GenericTypeReference<>(type)), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final URL url, final GenericType<T> type) {
        this.context.log().debug("Reading content from url " + url + " to type " + type.type().getTypeName());
        return Attempt.of(() -> this.configureMapper().readValue(url, new GenericTypeReference<>(type)), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> read(final InputStream stream, final GenericType<T> type) {
        this.context.log().debug("Reading content from input stream to type " + type.type().getTypeName());
        return Attempt.of(() -> this.configureMapper().readValue(stream, new GenericTypeReference<>(type)), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> update(final T object, final String content, final Class<T> type) {
        this.context.log().debug("Updating object " + object + " with content from string value to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readerForUpdating(object).readValue(content, type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> update(final T object, final Path path, final Class<T> type) {
        this.context.log().debug("Updating object " + object + " with content from path " + path + " to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readerForUpdating(object).readValue(path.toFile(), type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> update(final T object, final URL url, final Class<T> type) {
        this.context.log().debug("Updating object " + object + " with content from url " + url + " to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readerForUpdating(object).readValue(url, type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<T, ObjectMappingException> update(final T object, final InputStream stream, final Class<T> type) {
        this.context.log().debug("Updating object " + object + " with content from input stream to type " + type.getName());
        return Attempt.of(() -> this.configureMapper().readerForUpdating(object).readValue(stream, type), IOException.class)
                .mapError(ObjectMappingException::new);
    }

    @Override
    public <T> Attempt<Boolean, ObjectMappingException> write(final Path path, final T content) {
        if (content == null) return Attempt.of(false);

        this.context.log().debug("Writing content of type " + content.getClass().getSimpleName() + " to path " + path);
        if (content instanceof String string) return this.writePlain(path, string);
        return Attempt.of(() -> {
            this.writer(content).writeValue(path.toFile(), content);
            return true;
        }, IOException.class)
                .mapError(ObjectMappingException::new)
                .orCompute(() -> false);
    }

    @Override
    public <T> Attempt<Boolean, ObjectMappingException> write(final OutputStream outputStream, final T content) {
        if (content == null) return Attempt.of(false);

        this.context.log().debug("Writing content of type " + content.getClass().getSimpleName() + " to output stream");
        if (content instanceof String string) return this.writePlain(outputStream, string);
        return Attempt.of(() -> {
            this.writer(content).writeValue(outputStream, content);
            return true;
        }, IOException.class)
                .mapError(ObjectMappingException::new)
                .orCompute(() -> false);
    }

    @Override
    public <T> Attempt<String, ObjectMappingException> write(final T content) {
        if (content == null) return Attempt.of("");
        this.context.log().debug("Writing content of type " + content.getClass().getSimpleName() + " to string value");
        return Attempt.of(() -> this.writer(content).writeValueAsString(content), IOException.class)
                .mapError(ObjectMappingException::new)
                .map(out -> out.replace("\\r", ""));
    }

    protected Attempt<Boolean, ObjectMappingException> writePlain(final Path path, final String content) {
        try (final FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
            writer.flush();
            return Attempt.of(true);
        }
        catch (final IOException e) {
            return Attempt.of(false, new ObjectMappingException(e));
        }
    }

    protected Attempt<Boolean, ObjectMappingException> writePlain(final OutputStream outputStream, final String content) {
        try (final OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(content);
            writer.flush();
            return Attempt.of(true);
        }
        catch (final IOException e) {
            return Attempt.of(false, new ObjectMappingException(e));
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

    @Override
    public Map<String, Object> flat(final InputStream stream) {
        this.context.log().debug("Reading content from input stream to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(stream));
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
        final TypeView<Object> typeView = this.context.environment().introspect(content);
        final Option<Component> annotated = typeView.annotations().get(Component.class);

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
        this.inclusionRule = modifier;
        this.objectMapper = null;
        return this;
    }

    public ObjectMapper configureMapper() {
        if (null == this.objectMapper) {
            this.context.log().debug("Internal object mapper was not configured yet, configuring now with filetype " + this.fileType());
            final MapperBuilder<?, ?> builder = this.context.get(JacksonObjectMapperConfigurator.class)
                    .configure(this.mapper(this.fileType()), this.fileType(), this.inclusionRule);
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
