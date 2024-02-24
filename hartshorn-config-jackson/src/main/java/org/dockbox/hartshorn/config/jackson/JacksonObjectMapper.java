/*
 * Copyright 2019-2024 the original author or authors.
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
import java.util.concurrent.Callable;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.config.DefaultObjectMapper;
import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.JsonInclusionRule;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import jakarta.inject.Inject;

/**
 * Jackson-based implementation of the {@link org.dockbox.hartshorn.config.ObjectMapper} interface. This
 * implementation uses Jackson's {@link ObjectMapper} to read and write objects. The backing {@link ObjectMapper}
 * is configured based on the {@link FileFormat} that is used. By default the {@link ObjectMapper} is configured
 * to use {@link FileFormats#JSON JSON} as the {@link FileFormat}.
 *
 * @see JacksonObjectMapperConfigurator
 * @see JacksonDataMapper
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public class JacksonObjectMapper extends DefaultObjectMapper {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonObjectMapper.class);

    private final ApplicationContext context;
    private final JacksonDataMapperRegistry dataMapperRegistry;
    private final JacksonObjectMapperConfigurator configurator;

    private ObjectMapper objectMapper;
    private JsonInclusionRule inclusionRule;

    @Inject
    public JacksonObjectMapper(
            ApplicationContext applicationContext,
            JacksonDataMapperRegistry dataMapperRegistry,
            JacksonObjectMapperConfigurator configurator
    ) {
        super(FileFormats.JSON);
        this.context = applicationContext;
        this.dataMapperRegistry = dataMapperRegistry;
        this.configurator = configurator;
    }

    @Override
    public <T> Option<T> read(String content, Class<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from string value to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(content, type)));
    }

    @Override
    public <T> Option<T> read(Path path, Class<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from path " + path + " to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(path.toFile(), type)));
    }

    @Override
    public <T> Option<T> read(URL url, Class<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from url " + url + " to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(url, type)));
    }

    @Override
    public <T> Option<T> read(InputStream stream, Class<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from input stream to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(stream, type)));
    }

    @Override
    public <T> Option<T> read(String content, GenericType<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from string value to type " + type.type().getTypeName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(content, new GenericTypeReference<>(type))));
    }

    @Override
    public <T> Option<T> read(Path path, GenericType<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from path " + path + " to type " + type.type().getTypeName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(path.toFile(), new GenericTypeReference<>(type))));
    }

    @Override
    public <T> Option<T> read(URL url, GenericType<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from url " + url + " to type " + type.type().getTypeName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(url, new GenericTypeReference<>(type))));
    }

    @Override
    public <T> Option<T> read(InputStream stream, GenericType<T> type) throws ObjectMappingException {
        LOG.debug("Reading content from input stream to type " + type.type().getTypeName());
        return this.processStep(() -> Option.of(this.configureMapper().readValue(stream, new GenericTypeReference<>(type))));
    }

    @Override
    public <T> Option<T> update(T object, String content, Class<T> type) throws ObjectMappingException {
        LOG.debug("Updating object " + object + " with content from string value to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readerForUpdating(object).readValue(content, type)));
    }

    @Override
    public <T> Option<T> update(T object, Path path, Class<T> type) throws ObjectMappingException {
        LOG.debug("Updating object " + object + " with content from path " + path + " to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readerForUpdating(object).readValue(path.toFile(), type)));
    }

    @Override
    public <T> Option<T> update(T object, URL url, Class<T> type) throws ObjectMappingException {
        LOG.debug("Updating object " + object + " with content from url " + url + " to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readerForUpdating(object).readValue(url, type)));
    }

    @Override
    public <T> Option<T> update(T object, InputStream stream, Class<T> type) throws ObjectMappingException {
        LOG.debug("Updating object " + object + " with content from input stream to type " + type.getName());
        return this.processStep(() -> Option.of(this.configureMapper().readerForUpdating(object).readValue(stream, type)));
    }

    @Override
    public <T> boolean write(Path path, T content) throws ObjectMappingException {
        if (content == null) {
            return false;
        }

        LOG.debug("Writing content of type " + content.getClass().getSimpleName() + " to path " + path);
        if (content instanceof String string) {
            this.writePlain(path, string);
            return true;
        }

        this.processStep(() -> this.writer(content).writeValue(path.toFile(), content));
        return true;
    }

    @Override
    public <T> boolean write(OutputStream outputStream, T content) throws ObjectMappingException {
        if (content == null) {
            return false;
        }

        LOG.debug("Writing content of type " + content.getClass().getSimpleName() + " to output stream");
        if (content instanceof String string) {
            this.writePlain(outputStream, string);
            return true;
        }

        this.processStep(() -> this.writer(content).writeValue(outputStream, content));
        return true;
    }

    @Override
    public <T> String write(T content) throws ObjectMappingException {
        if (content == null) {
            return "";
        }
        LOG.debug("Writing content of type " + content.getClass().getSimpleName() + " to string value");
        return this.processStep(() -> this.writer(content).writeValueAsString(content)).replace("\\r", "");
    }

    protected void writePlain(Path path, String content) throws ObjectMappingException {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
            writer.flush();
        }
        catch (IOException e) {
            throw new ObjectMappingException(e);
        }
    }

    protected void writePlain(OutputStream outputStream, String content) throws ObjectMappingException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(content);
            writer.flush();
        }
        catch (IOException e) {
            throw new ObjectMappingException(e);
        }
    }

    @Override
    public Map<String, Object> flat(String content) throws ObjectMappingException {
        LOG.debug("Reading content from string value to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(content));
    }

    @Override
    public Map<String, Object> flat(Path path) throws ObjectMappingException {
        LOG.debug("Reading content from path " + path + " to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(path.toFile()));
    }

    @Override
    public Map<String, Object> flat(URL url) throws ObjectMappingException {
        LOG.debug("Reading content from url " + url + " to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(url));
    }

    @Override
    public Map<String, Object> flat(InputStream stream) throws ObjectMappingException {
        LOG.debug("Reading content from input stream to flat tree model");
        return this.flatInternal(() -> this.configureMapper().readTree(stream));
    }

    private Map<String, Object> flatInternal(FlatNodeSupplier node) throws ObjectMappingException {
        Map<String, Object> flat = new HashMap<>();
        try {
            JsonNode jsonNode = node.get();
            this.addKeys("", jsonNode, flat);
        }
        catch (FileNotFoundException e) {
            throw new ObjectMappingException("File not found: " + e.getMessage(), e);
        }
        catch (IOException e) {
            throw new ObjectMappingException("Failed to read object", e);
        }
        return flat;
    }

    private ObjectWriter writer(Object content) {
        ObjectWriter writer = this.configureMapper().writerWithDefaultPrettyPrinter();
        TypeView<Object> typeView = this.context.environment().introspector().introspect(content);
        Option<Component> annotated = typeView.annotations().get(Component.class);

        // Currently, only XML supports changing the root name, if XML is used we can change the
        // root name to be equal to the ID of the component.
        if (this.fileType().equals(FileFormats.XML) && annotated.present()) {
            Component annotation = annotated.get();
            writer = writer.withRootName(annotation.id());
        }
        return writer;
    }

    @Override
    public JacksonObjectMapper fileType(FileFormat fileFormat) {
        super.fileType(fileFormat);
        this.objectMapper = null;
        return this;
    }

    @Override
    public JacksonObjectMapper skipBehavior(JsonInclusionRule modifier) {
        this.inclusionRule = modifier;
        this.objectMapper = null;
        return this;
    }

    protected ObjectMapper configureMapper() {
        if (null == this.objectMapper) {
            LOG.debug("Internal object mapper was not configured yet, configuring now with filetype " + this.fileType());
            MapperBuilder<?, ?> builder = this.configurator
                    .configure(this.mapper(this.fileType()), this.fileType(), this.inclusionRule);
            this.objectMapper = builder.build();
        }
        return this.objectMapper;
    }

    protected MapperBuilder<?, ?> mapper(FileFormat fileFormat) {
        return this.dataMapperRegistry.resolve(fileFormat)
                .map(JacksonDataMapper::get)
                .orNull();
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
        }
        else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                this.addKeys(currentPath + "[" + i + "]", arrayNode.get(i), map);
            }
        }
        else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, this.configureMapper().convertValue(valueNode, Object.class));
        }
    }

    private <T> T processStep(Callable<T> supplier) throws ObjectMappingException {
        try {
            return supplier.call();
        }
        catch (Exception e) {
            throw new ObjectMappingException(e);
        }
    }

    private void processStep(WriterFunction runnable) throws ObjectMappingException {
        try {
            runnable.write();
        }
        catch (Exception e) {
            throw new ObjectMappingException(e);
        }
    }
}
