package org.dockbox.hartshorn.profiles.loader.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.dockbox.hartshorn.profiles.SimpleProfileProperty;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;

public class JsonNodeProfilePropertyCollector {

    private final Set<ValueProfileProperty> properties = new HashSet<>();

    public synchronized Set<ValueProfileProperty> collectProperties(JsonNode node) {
        this.properties.clear();
        Deque<String> path = new ArrayDeque<>();
        this.visitNode(node, path);
        if (!path.isEmpty()) {
            throw new IllegalStateException("Path was not cleared correctly");
        }
        return Set.copyOf(this.properties);
    }

    private void visitNode(JsonNode node, Deque<String> path) {
        if (node.isArray()) {
            this.visitArray(node, path);
        }
        else if (node.isObject()) {
            this.visitObject(node, path);
        }
        else if (node.isValueNode() || node.isNull()) {
            this.visitValue(node, path);
        }
        else {
            throw new IllegalStateException("Unknown node type: " + node.getNodeType());
        }
    }

    private void visitArray(Iterable<JsonNode> node, Deque<String> path) {
        int i = 0;
        for(JsonNode jsonNode : node) {
            path.addLast("[%s]".formatted(i));
            this.visitNode(jsonNode, path);
            path.removeLast();
            i++;
        }
    }

    private void visitObject(JsonNode node, Deque<String> path) {
        ObjectNode jsonNodes = (ObjectNode) node;
        jsonNodes.fields().forEachRemaining(entry -> {
            path.addLast((path.isEmpty() ? "" : ".") + entry.getKey());
            this.visitNode(entry.getValue(), path);
            path.removeLast();
        });
    }

    private void visitValue(JsonNode node, Deque<String> path) {
        String propertyName = this.absolutePath(path);
        this.properties.add(new SimpleProfileProperty(propertyName, node.asText()));
    }

    private String absolutePath(Iterable<String> path) {
        StringBuilder builder = new StringBuilder();
        for (String jsonNode : path) {
            builder.append(jsonNode);
        }
        return builder.toString();
    }
}
