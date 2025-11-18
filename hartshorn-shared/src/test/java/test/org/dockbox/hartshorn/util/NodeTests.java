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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.properties.ArrayNode;
import org.dockbox.hartshorn.util.properties.GroupNode;
import org.dockbox.hartshorn.util.properties.Node;
import org.dockbox.hartshorn.util.properties.NodeVisitor;
import org.dockbox.hartshorn.util.properties.SimpleNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class NodeTests {

    @Test
    void simpleNodeCanHaveNullValue() {
        Node<Object> node = new SimpleNode<>("node", null);
        assertThat(node.value()).isNull();
    }

    @Test
    void arrayNodeIsNonNull() {
        Node<List<Object>> node = new ArrayNode<>("node");
        assertThat(node.value()).isNotNull();
        assertThat(node.value()).isEmpty();
    }

    @Test
    void arrayNodeUsesInsertionOrder() {
        Node<List<Object>> node = new ArrayNode<>("node", "a", "b", "c");
        assertThat(node.value().get(0)).isEqualTo("a");
        assertThat(node.value().get(1)).isEqualTo("b");
        assertThat(node.value().get(2)).isEqualTo("c");
    }

    @Test
    void groupNodeIsNonNull() {
        Node<List<Node<?>>> node = new GroupNode("node");
        assertThat(node.value()).isNotNull();
        assertThat(node.value()).isEmpty();
    }

    @Test
    void groupNodeUsesInsertionOrder() {
        GroupNode node = new GroupNode("node");
        node.add(new SimpleNode<>("a", "a"));
        node.add(new SimpleNode<>("b", "b"));
        node.add(new SimpleNode<>("c", "c"));
        assertThat(node.value().get(0).value()).isEqualTo("a");
        assertThat(node.value().get(1).value()).isEqualTo("b");
        assertThat(node.value().get(2).value()).isEqualTo("c");
    }

    @Test
    void simpleNodeValueVisitor() {
        Node<Integer> node = new SimpleNode<>("node", 12);
        node.accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(Node<?> node) {
                assertThat(node.value()).isEqualTo(12);
                return null;
            }

            @Override
            public Void visit(GroupNode node) {
                fail("GroupNode should not be visited");
                return null;
            }

            @Override
            public Void visit(ArrayNode<?> node) {
                fail("ArrayNode should not be visited");
                return null;
            }
        });
    }

    @Test
    void groupNodeValueVisitor() {
        GroupNode node = new GroupNode("node");
        node.accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(Node<?> node) {
                fail("Node should not be visited");
                return null;
            }

            @Override
            public Void visit(GroupNode node) {
                assertThat(node.name()).isEqualTo("node");
                return null;
            }

            @Override
            public Void visit(ArrayNode<?> node) {
                fail("ArrayNode should not be visited");
                return null;
            }
        });
    }

    @Test
    void arrayNodeValueVisitor() {
        ArrayNode<Integer> node = new ArrayNode<>("node");
        node.accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(Node<?> node) {
                fail("Node should not be visited");
                return null;
            }

            @Override
            public Void visit(GroupNode node) {
                fail("GroupNode should not be visited");
                return null;
            }

            @Override
            public Void visit(ArrayNode<?> node) {
                assertThat(node.name()).isEqualTo("node");
                return null;
            }
        });
    }
}
