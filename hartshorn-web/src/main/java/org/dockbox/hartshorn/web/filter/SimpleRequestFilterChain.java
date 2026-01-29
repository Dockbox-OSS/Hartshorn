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

package org.dockbox.hartshorn.web.filter;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.report.RequestFilterReporter;

import java.util.List;

/**
 * A simple implementation of a {@link RequestFilterChain} that processes a list of
 * {@link RequestFilter filters} in order.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleRequestFilterChain implements RequestFilterChain, Reportable {

    private final List<RequestFilter> filters;
    private final int index;

    /**
     * Constructs a new {@link SimpleRequestFilterChain} with the given list of filters, starting at
     * index 0. Provided filters must be provided in the order they should be executed.
     *
     * @param filters The list of filters to process.
     */
    public SimpleRequestFilterChain(List<RequestFilter> filters) {
        this(filters, 0);
    }

    /**
     * Constructs a new {@link SimpleRequestFilterChain} with the given list of filters and starting
     * index.
     *
     * @param filters The list of filters to process.
     * @param index The current index in the filter chain.
     */
    public SimpleRequestFilterChain(List<RequestFilter> filters, int index) {
        this.filters = filters;
        this.index = index;
    }

    @Override
    public void accept(WebRequest request, WebResponse response) throws Exception {
        if (this.index < this.filters.size()) {
            RequestFilter currentFilter = this.filters.get(this.index);
            RequestFilterChain nextChain = new SimpleRequestFilterChain(
                this.filters,
                this.index + 1
            );
            currentFilter.handle(request, response, nextChain);
        }
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("filters").writeDelegates(this.filters.stream()
                .map(RequestFilterReporter::new)
                .toArray(Reportable[]::new));
    }
}
