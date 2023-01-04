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

package org.dockbox.hartshorn.reporting.collect;

import org.dockbox.hartshorn.util.ArrayNode;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.util.GroupNode;
import org.dockbox.hartshorn.util.Node;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.SimpleNode;
import org.dockbox.hartshorn.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;

public class StandardDiagnosticsPropertyWriter implements DiagnosticsPropertyWriter {

    private boolean closed = false;
    private final String name;
    private final StandardDiagnosticsReportCollector collector;
    private final GroupNode group;

    public StandardDiagnosticsPropertyWriter(final String name, final StandardDiagnosticsReportCollector collector, final GroupNode group) {
        this.name = name;
        this.collector = collector;
        this.group = group;
    }

    @Override
    public DiagnosticsReportCollector write(final String value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector write(final int value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector write(final long value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector write(final float value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector write(final double value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector write(final boolean value) {
        this.checkClosed();
        return this.exit(new SimpleNode<>(this.name, value));
    }

    @Override
    public DiagnosticsReportCollector write(final Reportable reportable) {
        this.checkClosed();
        final GroupNode group = new GroupNode(this.name);
        reportable.report(property -> new StandardDiagnosticsPropertyWriter(property, this.collector, group));
        return this.exit(group);
    }

    @Override
    public DiagnosticsReportCollector write(final String... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, values));
    }

    @Override
    public DiagnosticsReportCollector write(final int... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector write(final long... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector write(final float... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector write(final double... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector write(final boolean... values) {
        this.checkClosed();
        return this.exit(new ArrayNode<>(this.name, TypeUtils.stream(values).toList()));
    }

    @Override
    public DiagnosticsReportCollector write(final Reportable... reportables) {
        final List<Node<?>> nodes = new ArrayList<>();
        for (final Reportable reportable : reportables) {
            final GroupNode group = new GroupNode(this.name);
            reportable.report(property -> new StandardDiagnosticsPropertyWriter(property, this.collector, group));
            nodes.add(group);
        }
        return this.exit(new ArrayNode<>(this.name, nodes));
    }

    private void checkClosed() {
        if (this.closed) throw new IllegalStateException("Property writer is closed");
    }
    
    private DiagnosticsReportCollector exit(final Node<?> node) {
        this.group.add(node);
        this.closed = true;
        return this.collector;
    }
}
