/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.reporting.serialize;

import org.dockbox.hartshorn.util.properties.ArrayNode;
import org.dockbox.hartshorn.util.properties.GroupNode;
import org.dockbox.hartshorn.util.properties.Node;
import org.dockbox.hartshorn.util.properties.NodeVisitor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link NodeVisitor} which converts a {@link Node} to a {@link JsonNode}. This is useful for
 * serialization using Jackson.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
class NodeToJacksonVisitor implements NodeVisitor<JsonNode> {

    @Override
    public JsonNode visit(Node<?> node) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        Object value = node.value();
        return switch (value) {
            case String stringValue -> factory.stringNode(stringValue);
            case Integer integerValue -> factory.numberNode(integerValue);
            case Double doubleValue -> factory.numberNode(doubleValue);
            case Long longValue -> factory.numberNode(longValue);
            case Short shortValue -> factory.numberNode(shortValue);
            case Boolean booleanValue -> factory.booleanNode(booleanValue);
            case Node<?> nodeValue -> nodeValue.accept(this);
            case null -> factory.nullNode();
            default -> throw new IllegalArgumentException("Unsupported type " + value.getClass()
                .getName() + " at node " + node.name());
        };
    }

    @Override
    public JsonNode visit(GroupNode node) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode object = factory.objectNode();
        for (Node<?> value : node.value()) {
            object.set(value.name(), value.accept(this));
        }
        return object;
    }

    @Override
    public JsonNode visit(ArrayNode<?> node) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        List<JsonNode> nodes = new ArrayList<>();
        for (Object value : node.value()) {
            switch (value) {
                case String stringValue -> nodes.add(factory.textNode(stringValue));
                case Integer integerValue -> nodes.add(factory.numberNode(integerValue));
                case Double doubleValue -> nodes.add(factory.numberNode(doubleValue));
                case Long longValue -> nodes.add(factory.numberNode(longValue));
                case Short shortValue -> nodes.add(factory.numberNode(shortValue));
                case Boolean booleanValue -> nodes.add(factory.booleanNode(booleanValue));
                case Node<?> nodeValue -> nodes.add(nodeValue.accept(this));
                case null -> throw new IllegalArgumentException("Unsupported type null");
                default -> throw new IllegalArgumentException("Unsupported type " + value.getClass()
                    .getName());
            }
        }
        return factory.arrayNode().addAll(nodes);
    }
}
