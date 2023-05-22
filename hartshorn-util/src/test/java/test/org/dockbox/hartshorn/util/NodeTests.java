/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.util.ArrayNode;
import org.dockbox.hartshorn.util.GroupNode;
import org.dockbox.hartshorn.util.Node;
import org.dockbox.hartshorn.util.NodeVisitor;
import org.dockbox.hartshorn.util.SimpleNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class NodeTests {

    @Test
    void testSimpleNodeCanHaveNullValue() {
        final Node<Object> node = new SimpleNode<>("node", null);
        Assertions.assertNull(node.value());
    }

    @Test
    void testArrayNodeIsNonNull() {
        final Node<List<Object>> node = new ArrayNode<>("node");
        Assertions.assertNotNull(node.value());
        Assertions.assertTrue(node.value().isEmpty());
    }

    @Test
    void testArrayNodeUsesInsertionOrder() {
        final Node<List<Object>> node = new ArrayNode<>("node", "a", "b", "c");
        Assertions.assertEquals("a", node.value().get(0));
        Assertions.assertEquals("b", node.value().get(1));
        Assertions.assertEquals("c", node.value().get(2));
    }

    @Test
    void testGroupNodeIsNonNull() {
        final Node<List<Node<?>>> node = new GroupNode("node");
        Assertions.assertNotNull(node.value());
        Assertions.assertTrue(node.value().isEmpty());
    }

    @Test
    void testGroupNodeUsesInsertionOrder() {
        final GroupNode node = new GroupNode("node");
        node.add(new SimpleNode<>("a", "a"));
        node.add(new SimpleNode<>("b", "b"));
        node.add(new SimpleNode<>("c", "c"));
        Assertions.assertEquals("a", node.value().get(0).value());
        Assertions.assertEquals("b", node.value().get(1).value());
        Assertions.assertEquals("c", node.value().get(2).value());
    }

    @Test
    void testSimpleNodeValueVisitor() {
        final Node<Integer> node = new SimpleNode<>("node", 12);
        node.accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(final Node<?> node) {
                Assertions.assertEquals(12, node.value());
                return null;
            }

            @Override
            public Void visit(final GroupNode node) {
                Assertions.fail("GroupNode should not be visited");
                return null;
            }

            @Override
            public Void visit(final ArrayNode<?> node) {
                Assertions.fail("ArrayNode should not be visited");
                return null;
            }
        });
    }

    @Test
    void testGroupNodeValueVisitor() {
        final GroupNode node = new GroupNode("node");
        node.accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(final Node<?> node) {
                Assertions.fail("Node should not be visited");
                return null;
            }

            @Override
            public Void visit(final GroupNode node) {
                Assertions.assertEquals("node", node.name());
                return null;
            }

            @Override
            public Void visit(final ArrayNode<?> node) {
                Assertions.fail("ArrayNode should not be visited");
                return null;
            }
        });
    }

    @Test
    void testArrayNodeValueVisitor() {
        final ArrayNode<Integer> node = new ArrayNode<>("node");
        node.accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(final Node<?> node) {
                Assertions.fail("Node should not be visited");
                return null;
            }

            @Override
            public Void visit(final GroupNode node) {
                Assertions.fail("GroupNode should not be visited");
                return null;
            }

            @Override
            public Void visit(final ArrayNode<?> node) {
                Assertions.assertEquals("node", node.name());
                return null;
            }
        });
    }
}
