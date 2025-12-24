/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.properties.loader.support;

import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.loader.FilePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.PredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathFormatter;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathNode;
import org.dockbox.hartshorn.properties.loader.path.PropertyRootPathNode;
import org.dockbox.hartshorn.util.IOUtilities;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.stream.EntryStream;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.ValueNode;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PredicatePropertyRegistryLoader} that loads properties using a Jackson
 * {@link ObjectMapper}. Depending on the implementation of the {@link ObjectMapper}, different file
 * formats can be supported.
 *
 * @see JacksonJavaPropsPropertyRegistryLoader
 * @see JacksonYamlPropertyRegistryLoader
 *
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
public abstract class JacksonPropertyRegistryLoader
        implements PredicatePropertyRegistryLoader, FilePropertyRegistryLoader {

    private final PropertyPathFormatter formatter;
    private ObjectMapper objectMapper;

    protected JacksonPropertyRegistryLoader(PropertyPathFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Creates a new instance of the {@link ObjectMapper} that is used to load the properties from
     * the file.
     *
     * @return a new instance of the {@link ObjectMapper}
     */
    protected abstract ObjectMapper createObjectMapper();

    @Override
    public void loadRegistry(PropertyRegistry registry, URI path) throws IOException {
        if (IOUtilities.hasFileExtension(path)) {
            String fileExtension = IOUtilities.getFileExtension(path);
            if (!this.supportedExtensions().contains(fileExtension)) {
                throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
            }
            JsonNode graph = this.loadGraph(path);
            this.loadRegistry(registry, graph);
        }
        else {
            for (String supportedExtension : this.supportedExtensions()) {
                URI withExtension = URI.create(path.toString() + "." + supportedExtension);
                if (IOUtilities.exists(withExtension)) {
                    JsonNode graph = this.loadGraph(withExtension);
                    this.loadRegistry(registry, graph);
                    return;
                }
            }
        }
    }

    /**
     * Loads the properties from the given {@link JsonNode} and registers them in the provided
     * {@link PropertyRegistry}.
     *
     * @param registry the registry to register the properties in
     * @param node the node to load the properties from
     */
    protected void loadRegistry(PropertyRegistry registry, JsonNode node) {
        List<ConfiguredProperty> properties = this.loadProperties(node);
        registry.registerAll(properties);
    }

    /**
     * Loads the properties from the given {@link JsonNode}. All
     * {@link ConfiguredProperty properties} returned will have the same
     * {@link PropertyRootPathNode} as their parent.
     *
     * @param node the node to load the properties from
     *
     * @return a list of {@link ConfiguredProperty properties} loaded from the given node
     */
    protected List<ConfiguredProperty> loadProperties(JsonNode node) {
        return this.loadProperties(new PropertyRootPathNode(), node);
    }

    /**
     * Loads the properties from the given {@link JsonNode} using the provided path. All
     * {@link ConfiguredProperty properties} returned will have the provided path as their parent.
     *
     * @param path the node to use as the parent of the properties
     * @param node the node to load the properties from
     *
     * @return a list of {@link ConfiguredProperty properties} loaded from the given node
     */
    protected List<ConfiguredProperty> loadProperties(PropertyPathNode path, JsonNode node) {
        return switch (node) {
            case ArrayNode arrayNode -> this.loadArrayProperties(path, arrayNode);
            case ObjectNode objectNode -> this.loadObjectProperties(path, objectNode);
            case ValueNode valueNode -> List.of(this.loadSingleProperty(path, valueNode));
            default ->
                throw new IllegalArgumentException("Invalid node type: " + node.getNodeType());
        };
    }

    /**
     * Loads a single property from the given {@link ValueNode} using the provided path.
     *
     * @param path the node to use as the parent of the property
     * @param valueNode the node to load the property from
     *
     * @return a single {@link ConfiguredProperty property} loaded from the given node
     */
    protected ConfiguredProperty loadSingleProperty(PropertyPathNode path, ValueNode valueNode) {
        String propertyPath = this.formatter.formatPath(path);
        return new SingleConfiguredProperty(propertyPath, valueNode.asText());
    }

    /**
     * Loads the properties from the given {@link ArrayNode} using the provided path. All
     * {@link ConfiguredProperty properties} returned will have a parent node for the array index.
     * The nodes for the array indexes will have the given path as their parent.
     *
     * @param path the node to use as the parent of the properties
     * @param arrayNode the node to load the properties from
     *
     * @return a list of {@link ConfiguredProperty properties} loaded from the given node
     */
    protected List<ConfiguredProperty> loadArrayProperties(
        PropertyPathNode path,
        ArrayNode arrayNode
    ) {
        List<ConfiguredProperty> properties = new ArrayList<>();
        CollectionUtilities.indexed(arrayNode.elements(), (index, element) -> {
            PropertyPathNode nextPath = path.index(index);
            properties.addAll(this.loadProperties(nextPath, element));
        });
        return properties;
    }

    /**
     * Loads the properties from the given {@link ObjectNode} using the provided path. All
     * {@link ConfiguredProperty properties} returned will have a parent node for their respective
     * field name. The nodes for the field names will have the given path as their parent.
     *
     * @param path the node to use as the parent of the properties
     * @param objectNode the node to load the properties from
     *
     * @return a list of {@link ConfiguredProperty properties} loaded from the given node
     */
    protected List<ConfiguredProperty> loadObjectProperties(
        PropertyPathNode path,
        ObjectNode objectNode
    ) {
        List<ConfiguredProperty> properties = new ArrayList<>();
        EntryStream.of(objectNode.properties())
            .forEach((name, value) -> {
                PropertyPathNode nextPath = path.property(name);
                properties.addAll(this.loadProperties(nextPath, value));
            });
        return properties;
    }

    /**
     * Loads the contents of the file at the given path into a
     * {@link JsonNode node-based tree structure}.
     *
     * @param path the path to the file to load
     *
     * @return a {@link JsonNode node-based tree structure} representing the contents of the file
     *
     * @throws IOException if an error occurs while reading the file
     */
    protected JsonNode loadGraph(URI path) throws IOException {
        return this.objectMapper().readTree(path.toURL().openStream());
    }

    /**
     * Returns the {@link ObjectMapper} that is used to load the properties from the file. If the
     * object mapper has not been created yet, a new instance is created using
     * {@link #createObjectMapper()}.
     *
     * @return the {@link ObjectMapper} that is used to load the properties from the file
     */
    protected ObjectMapper objectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = this.createObjectMapper();
        }
        return this.objectMapper;
    }

    @Override
    public boolean isCompatible(URI path) {
        return this.supportedExtensions().contains(IOUtilities.getFileExtension(path));
    }
}
