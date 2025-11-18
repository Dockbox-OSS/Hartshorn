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

package test.org.dockbox.hartshorn.reporting;

import org.dockbox.hartshorn.util.properties.ArrayNode;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.util.properties.GroupNode;
import org.dockbox.hartshorn.util.properties.Node;
import org.dockbox.hartshorn.util.properties.SimpleNode;
import org.dockbox.hartshorn.reporting.collect.StandardDiagnosticsPropertyWriter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class DiagnosticsPropertyWriterTests {

    protected DiagnosticsPropertyWriter writer(GroupNode group) {
        return new StandardDiagnosticsPropertyWriter("test", null, group);
    }

    @Test
    void writerClosingRejectsFurtherChanges() {
        DiagnosticsPropertyWriter writer = this.writer(new GroupNode(""));
        writer.writeString("test"); // Expecting auto-close
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> writer.writeString("test"));
    }

    @Test
    void integerPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeInt(1);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(SimpleNode.class);
        assertThat(node.value()).isEqualTo(1);
    }

    @Test
    void longPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeLong(2L);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(SimpleNode.class);
        assertThat(node.value()).isEqualTo(2L);
    }

    @Test
    void floatPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeFloat(3.0f);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(SimpleNode.class);
        assertThat(node.value()).isEqualTo(3.0f);
    }

    @Test
    void doublePropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDouble(4.0d);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(SimpleNode.class);
        assertThat(node.value()).isEqualTo(4.0d);
    }

    @Test
    void booleanPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeBoolean(true);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(SimpleNode.class);
        assertThat(node.value()).isEqualTo(true);
    }

    @Test
    void stringPropertyWritingCreatesSimpleNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeString("test");
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(SimpleNode.class);
        assertThat(node.value()).isEqualTo("test");
    }

    @Test
    void integerArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeInts(1, 2, 3);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        assertThat(values).hasSize(3);
        assertThat(values.get(0)).isEqualTo(1);
        assertThat(values.get(1)).isEqualTo(2);
        assertThat(values.get(2)).isEqualTo(3);
    }

    @Test
    void longArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeLongs(1L, 2L, 3L);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        assertThat(values).hasSize(3);
        assertThat(values.get(0)).isEqualTo(1L);
        assertThat(values.get(1)).isEqualTo(2L);
        assertThat(values.get(2)).isEqualTo(3L);
    }

    @Test
    void floatArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeFloats(1.0f, 2.0f, 3.0f);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        assertThat(values).hasSize(3);
        assertThat(values.get(0)).isEqualTo(1.0f);
        assertThat(values.get(1)).isEqualTo(2.0f);
        assertThat(values.get(2)).isEqualTo(3.0f);
    }

    @Test
    void doubleArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDoubles(1.0d, 2.0d, 3.0d);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        assertThat(values).hasSize(3);
        assertThat(values.get(0)).isEqualTo(1.0d);
        assertThat(values.get(1)).isEqualTo(2.0d);
        assertThat(values.get(2)).isEqualTo(3.0d);
    }

    @Test
    void booleanArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeBooleans(true, false, true);
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        assertThat(values).hasSize(3);
        assertThat(values.get(0)).isEqualTo(true);
        assertThat(values.get(1)).isEqualTo(false);
        assertThat(values.get(2)).isEqualTo(true);
    }

    @Test
    void stringArrayPropertyWritingCreatesArrayNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeStrings("test1", "test2", "test3");
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);
        ArrayNode<?> arrayNode = (ArrayNode<?>) node;
        List<?> values = arrayNode.value();
        assertThat(values).hasSize(3);
        assertThat(values.get(0)).isEqualTo("test1");
        assertThat(values.get(1)).isEqualTo("test2");
        assertThat(values.get(2)).isEqualTo("test3");
    }

    @Test
    void reportablePropertyWritingCreatesGroupNode() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDelegate(collector -> collector.property("test2").writeString("test2"));
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(GroupNode.class);

        GroupNode groupNode = (GroupNode) node;
        assertThat(groupNode.has("test2")).isTrue();
        assertThat(groupNode.get("test2").value()).isEqualTo("test2");
    }

    @Test
    void reportableArrayPropertyWritingCreatesArrayNodeOfGroups() {
        String propertyName = "test";
        GroupNode group = new GroupNode("test");
        DiagnosticsPropertyWriter writer = this.writer(group);

        writer.writeDelegates(
                collector -> collector.property("test2").writeString("test2"),
                collector -> collector.property("test3").writeString("test3")
        );
        assertThat(group.has(propertyName)).isTrue();

        Node<?> node = group.get(propertyName);
        assertThat(node).isInstanceOf(ArrayNode.class);

        ArrayNode<?> arrayNode = (ArrayNode<?>) node;

        assertThat(arrayNode.value()).hasSize(2);
        assertThat(arrayNode.value().get(0)).isInstanceOf(GroupNode.class);
        assertThat(arrayNode.value().get(1)).isInstanceOf(GroupNode.class);

        GroupNode groupNode1 = (GroupNode) arrayNode.value().get(0);
        assertThat(groupNode1.has("test2")).isTrue();
        assertThat(groupNode1.get("test2").value()).isEqualTo("test2");

        GroupNode groupNode2 = (GroupNode) arrayNode.value().get(1);
        assertThat(groupNode2.has("test3")).isTrue();
        assertThat(groupNode2.get("test3").value()).isEqualTo("test3");
    }
}
