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

package org.dockbox.hartshorn.reporting.collect;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Stream;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.ArrayNode;
import org.dockbox.hartshorn.util.GroupNode;
import org.dockbox.hartshorn.util.Node;
import org.dockbox.hartshorn.util.SimpleNode;
import org.dockbox.hartshorn.util.TypeUtils;

/**
 * A diagnostics property writer that writes to a {@link GroupNode} in a {@link StandardDiagnosticsReportCollector}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class StandardDiagnosticsPropertyWriter implements DiagnosticsPropertyWriter {

    private boolean closed = false;
    private final String name;
    private final StandardDiagnosticsReportCollector collector;
    private final GroupNode group;

    public StandardDiagnosticsPropertyWriter(String name, StandardDiagnosticsReportCollector collector, GroupNode group) {
        this.name = name;
        this.collector = collector;
        this.group = group;
    }

    @Override
    public DiagnosticsReportCollector writeString(String value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector writeInt(int value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector writeLong(long value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector writeFloat(float value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector writeDouble(double value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector writeBoolean(boolean value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public <E extends Enum<E>> DiagnosticsReportCollector writeEnum(E value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value.name()));
    }

    @Override
    public DiagnosticsReportCollector writeDelegate(Reportable reportable) {
        this.checkClosed();
        GroupNode group = new GroupNode(this.name);
        reportable.report(property -> new StandardDiagnosticsPropertyWriter(property, this.collector, group));
        return this.exit(group);
    }

    @Override
    public DiagnosticsReportCollector writeStrings(String... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, values));
    }

    @Override
    public DiagnosticsReportCollector writeInts(int... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector writeLongs(long... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector writeFloats(float... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector writeDoubles(double... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector writeBooleans(boolean... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public <E extends Enum<E>> DiagnosticsReportCollector writeEnums(E... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, Stream.of(values).map(Enum::name).toList()));
    }

    @Override
    public DiagnosticsReportCollector writeDelegates(Reportable... reportables) {
        List<Node<?>> nodes = new ArrayList<>();
        for (Reportable reportable : reportables) {
            GroupNode group = new GroupNode(this.name);
            reportable.report(property -> new StandardDiagnosticsPropertyWriter(property, this.collector, group));
            nodes.add(group);
        }
        return this.exit(new ArrayNode<>(this.name, nodes));
    }

    private void checkClosed() {
        if (this.closed) {
            throw new IllegalStateException("Property writer is closed");
        }
    }

    private DiagnosticsReportCollector exit(Node<?> node) {
        this.group.add(node);
        this.closed = true;
        return this.collector;
    }
}
