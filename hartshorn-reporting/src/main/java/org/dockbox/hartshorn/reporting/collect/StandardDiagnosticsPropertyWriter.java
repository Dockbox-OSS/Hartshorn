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

package org.dockbox.hartshorn.reporting.collect;

import org.dockbox.hartshorn.reporting.DiagnosticsReporter;

public class StandardDiagnosticsPropertyWriter implements DiagnosticsPropertyWriter {

    private boolean closed = false;
    private final String name;
    private final StandardDiagnosticsReportCollector collector;

    public StandardDiagnosticsPropertyWriter(final String name, final StandardDiagnosticsReportCollector collector) {
        this.name = name;
        this.collector = collector;
    }

    @Override
    public DiagnosticsReportCollector write(final String value) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final int value) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final long value) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final float value) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final double value) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final boolean value) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final DiagnosticsReporter reporter) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final String[] values) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final int[] values) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final long[] values) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final float[] values) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final double[] values) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final boolean[] values) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }

    @Override
    public DiagnosticsReportCollector write(final DiagnosticsReporter[] reporters) {
        this.checkClosed();
        // TODO: Implement
        return this.exit();
    }
    
    private void checkClosed() {
        if (this.closed) throw new IllegalStateException("Property writer is closed");
    }
    
    private DiagnosticsReportCollector exit() {
        this.closed = true;
        return this.collector;
    }
}
