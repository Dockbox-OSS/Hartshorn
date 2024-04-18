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

    protected DiagnosticsPropertyWriter writer(GroupNode group) {
        return new StandardDiagnosticsPropertyWriter("test", null, group);
    }

    @Test
    void testWriterClosingRejectsFurtherChanges() {
        DiagnosticsPropertyWriter writer = this.writer(new GroupNode(""));
        writer.writeString("test"); // Expecting auto-close
        Assertions.assertThrows(IllegalStateException.class, () -> writer.writeString("test"));
    }

    @Test
    void testIntegerPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeInt(1);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 1);
    }

    @Test
    void testLongPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeLong(2L);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 2L);
    }

    @Test
    void testFloatPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeFloat(3.0f);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 3.0f);
    }

    @Test
    void testDoublePropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDouble(4.0d);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), 4.0d);
    }

    @Test
    void testBooleanPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeBoolean(true);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), true);
    }

    @Test
    void testStringPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeString("test");
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof SimpleNode);
        Assertions.assertEquals(node.value(), "test");
    }

    @Test
    void testIntegerArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeInts(1, 2, 3);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1);
        Assertions.assertEquals(values.get(1), 2);
        Assertions.assertEquals(values.get(2), 3);
    }

    @Test
    void testLongArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeLongs(1L, 2L, 3L);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1L);
        Assertions.assertEquals(values.get(1), 2L);
        Assertions.assertEquals(values.get(2), 3L);
    }

    @Test
    void testFloatArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeFloats(1.0f, 2.0f, 3.0f);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1.0f);
        Assertions.assertEquals(values.get(1), 2.0f);
        Assertions.assertEquals(values.get(2), 3.0f);
    }

    @Test
    void testDoubleArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDoubles(1.0d, 2.0d, 3.0d);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), 1.0d);
        Assertions.assertEquals(values.get(1), 2.0d);
        Assertions.assertEquals(values.get(2), 3.0d);
    }

    @Test
    void testBooleanArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeBooleans(true, false, true);
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), true);
        Assertions.assertEquals(values.get(1), false);
        Assertions.assertEquals(values.get(2), true);
    }

    @Test
    void testStringArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeStrings("test1", "test2", "test3");
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        Assertions.assertEquals(values.size(), 3);
        Assertions.assertEquals(values.get(0), "test1");
        Assertions.assertEquals(values.get(1), "test2");
        Assertions.assertEquals(values.get(2), "test3");
    }

    @Test
    void testReportablePropertyWritingCreatesGroupNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDelegate(collector -> collector.property("test2").writeString("test2"));
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof GroupNode);

        GroupNode groupNode = (GroupNode) node;
        Assertions.assertTrue(groupNode.has("test2"));
        Assertions.assertEquals(groupNode.get("test2").value(), "test2");
    }

    @Test
    void testReportableArrayPropertyWritingCreatesArrayNodeOfGroups() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDelegates(
                collector -> collector.property("test2").writeString("test2"),
                collector -> collector.property("test3").writeString("test3")
        );
        Assertions.assertTrue(group.has(propertyName));

        Node<?> node = group.get(propertyName);
        Assertions.assertTrue(node instanceof ArrayNode<?>);

        ArrayNode<?> arrayNode = (ArrayNode<?>) node;

        Assertions.assertEquals(arrayNode.value().size(), 2);
        Assertions.assertTrue(arrayNode.value().get(0) instanceof GroupNode);
        Assertions.assertTrue(arrayNode.value().get(1) instanceof GroupNode);

        GroupNode groupNode1 = (GroupNode) arrayNode.value().get(0);
        Assertions.assertTrue(groupNode1.has("test2"));
        Assertions.assertEquals(groupNode1.get("test2").value(), "test2");

        GroupNode groupNode2 = (GroupNode) arrayNode.value().get(1);
        Assertions.assertTrue(groupNode2.has("test3"));
        Assertions.assertEquals(groupNode2.get("test3").value(), "test3");
    }
}
