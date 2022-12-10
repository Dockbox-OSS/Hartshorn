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

package test.org.dockbox.hartshorn.reporting;

import org.dockbox.hartshorn.util.ArrayNode;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.util.GroupNode;
import org.dockbox.hartshorn.util.Node;
import org.dockbox.hartshorn.util.SimpleNode;
import org.dockbox.hartshorn.reporting.collect.StandardDiagnosticsPropertyWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DiagnosticsPropertyWriterTests {

    protected DiagnosticsPropertyWriter writer(final GroupNode group) {
        return new StandardDiagnosticsPropertyWriter("test", null, group);
    }

    @Test
    void testWriterClosingRejectsFurtherChanges() {
        final DiagnosticsPropertyWriter writer = this.writer(new GroupNode(""));
        writer.write("test"); // Expecting auto-close
        Assertions.assertThrows(IllegalStateException.class, () -> writer.write("test"));
    }

    @Test
    void testIntegerPropertyWritingCreatesSimpleNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(1);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 1);
    }

    @Test
    void testLongPropertyWritingCreatesSimpleNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(2L);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 2L);
    }

    @Test
    void testFloatPropertyWritingCreatesSimpleNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(3.0f);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 3.0f);
    }

    @Test
    void testDoublePropertyWritingCreatesSimpleNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(4.0d);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 4.0d);
    }

    @Test
    void testBooleanPropertyWritingCreatesSimpleNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(true);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), true);
    }

    @Test
    void testStringPropertyWritingCreatesSimpleNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write("test");
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), "test");
    }

    @Test
    void testIntegerArrayPropertyWritingCreatesArrayNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(1, 2, 3);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        final List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1);
        Assertions.assertEquals(values.get(1), 2);
        Assertions.assertEquals(values.get(2), 3);
    }

    @Test
    void testLongArrayPropertyWritingCreatesArrayNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(1L, 2L, 3L);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        final List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1L);
        Assertions.assertEquals(values.get(1), 2L);
        Assertions.assertEquals(values.get(2), 3L);
    }

    @Test
    void testFloatArrayPropertyWritingCreatesArrayNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(1.0f, 2.0f, 3.0f);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        final List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1.0f);
        Assertions.assertEquals(values.get(1), 2.0f);
        Assertions.assertEquals(values.get(2), 3.0f);
    }

    @Test
    void testDoubleArrayPropertyWritingCreatesArrayNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(1.0d, 2.0d, 3.0d);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        final List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1.0d);
        Assertions.assertEquals(values.get(1), 2.0d);
        Assertions.assertEquals(values.get(2), 3.0d);
    }

    @Test
    void testBooleanArrayPropertyWritingCreatesArrayNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(true, false, true);
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        final List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), true);
        Assertions.assertEquals(values.get(1), false);
        Assertions.assertEquals(values.get(2), true);
    }

    @Test
    void testStringArrayPropertyWritingCreatesArrayNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write("test1", "test2", "test3");
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        final List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), "test1");
        Assertions.assertEquals(values.get(1), "test2");
        Assertions.assertEquals(values.get(2), "test3");
    }

    @Test
    void testReportablePropertyWritingCreatesGroupNode() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(collector -> collector.property("test2").write("test2"));
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof GroupNode);

        final GroupNode groupNode = (GroupNode) node;
        Assertions.assertTrue(groupNode.has("test2"));
        Assertions.assertEquals(groupNode.get("test2").value(), "test2");
    }

    @Test
    void testReportableArrayPropertyWritingCreatesArrayNodeOfGroups() {
        final String propertyName = "test";
        final GroupNode group = new GroupNode("test");
        final DiagnosticsPropertyWriter writer = this.writer(group);

        writer.write(
                collector -> collector.property("test2").write("test2"),
                collector -> collector.property("test3").write("test3")
        );
        Assertions.assertTrue(group.has(propertyName));

        final Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode<?>);

        final ArrayNode<?> arrayNode = (ArrayNode<?>) node;

        Assertions.assertEquals(arrayNode.value().size(), 2);
        Assertions.assertTrue(arrayNode.value().get(0) instanceof GroupNode);
        Assertions.assertTrue(arrayNode.value().get(1) instanceof GroupNode);

        final GroupNode groupNode1 = (GroupNode) arrayNode.value().get(0);
        Assertions.assertTrue(groupNode1.has("test2"));
        Assertions.assertEquals(groupNode1.get("test2").value(), "test2");

        final GroupNode groupNode2 = (GroupNode) arrayNode.value().get(1);
        Assertions.assertTrue(groupNode2.has("test3"));
        Assertions.assertEquals(groupNode2.get("test3").value(), "test3");
    }
}
