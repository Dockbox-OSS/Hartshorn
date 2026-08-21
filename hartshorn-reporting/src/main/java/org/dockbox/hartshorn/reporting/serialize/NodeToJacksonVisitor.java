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
        Object value = node.value();
        return getJsonNode(value);
    }

    @Override
    public JsonNode visit(GroupNode node) {
        ObjectNode object = JsonNodeFactory.instance.objectNode();
        for (Node<?> value : node.value()) {
            object.set(value.name(), value.accept(this));
        }
        return object;
    }

    @Override
    public JsonNode visit(ArrayNode<?> node) {
        return JsonNodeFactory.instance.arrayNode().addAll(node.value().stream()
                .map(this::getJsonNode)
                .toList());
    }

    private JsonNode getJsonNode(Object value) {
        return switch (value) {
            case String stringValue -> JsonNodeFactory.instance.stringNode(stringValue);
            case Integer integerValue -> JsonNodeFactory.instance.numberNode(integerValue);
            case Double doubleValue -> JsonNodeFactory.instance.numberNode(doubleValue);
            case Long longValue -> JsonNodeFactory.instance.numberNode(longValue);
            case Short shortValue -> JsonNodeFactory.instance.numberNode(shortValue);
            case Boolean booleanValue -> JsonNodeFactory.instance.booleanNode(booleanValue);
            case Node<?> nodeValue -> nodeValue.accept(this);
            case null -> JsonNodeFactory.instance.nullNode();
            default -> throw new IllegalArgumentException("Unsupported type " + value.getClass()
                .getName());
        };
    }
}
