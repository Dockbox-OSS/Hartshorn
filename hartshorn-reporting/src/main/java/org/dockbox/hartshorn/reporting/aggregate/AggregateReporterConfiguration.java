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

package org.dockbox.hartshorn.reporting.aggregate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;

/**
 * A configuration for an {@link AggregateDiagnosticsReporter}. This configuration is used to register the reporters
 * that are aggregated by the {@link AggregateDiagnosticsReporter}. No ordering is guaranteed.
 *
 * @since 0.5.0
 *
 * @see AggregateDiagnosticsReporter
 *
 * @author Guus Lieben
 */
public class AggregateReporterConfiguration {

    private final Map<String, CategorizedDiagnosticsReporter> reporters = new LinkedHashMap<>();

    /**
     * Registers the given reporter. If a reporter with the same category is already registered, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param reporter the reporter to register
     *
     * @throws IllegalArgumentException when a reporter with the same category is already registered
     */
    public void add(CategorizedDiagnosticsReporter reporter) {
        if (this.reporters.containsKey(reporter.category())) {
            throw new IllegalArgumentException("Reporter with category '" + reporter.category() + "' already registered");
        }
        this.reporters.put(reporter.category(), reporter);
    }

    /**
     * Registers all given reporters. If a reporter with the same category is already registered, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param reporters the reporters to register
     *
     * @throws IllegalArgumentException when a reporter with the same category is already registered
     */
    public void addAll(Collection<CategorizedDiagnosticsReporter> reporters) {
        reporters.forEach(this::add);
    }

    /**
     * Returns a set of all registered reporters.
     *
     * @return a set of all registered reporters
     */
    public Set<CategorizedDiagnosticsReporter> reporters() {
        return Set.copyOf(this.reporters.values());
    }
}
