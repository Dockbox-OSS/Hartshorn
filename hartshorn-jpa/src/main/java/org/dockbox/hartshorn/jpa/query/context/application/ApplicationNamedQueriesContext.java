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

package org.dockbox.hartshorn.jpa.query.context.application;

import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;

@InstallIfAbsent
public class ApplicationNamedQueriesContext extends DefaultProvisionContext implements Reportable {

    private final Map<String, ComponentNamedQueryContext> namedQueries = new ConcurrentHashMap<>();

    public void add(final ComponentNamedQueryContext context) {
        this.namedQueries.put(context.name(), context);
    }

    public Option<ComponentNamedQueryContext> get(final String name) {
        return Option.of(() -> this.namedQueries.get(name));
    }

    public boolean contains(final String name) {
        return this.namedQueries.containsKey(name);
    }

    public Map<String, ComponentNamedQueryContext> namedQueries() {
        return this.namedQueries;
    }

    public void process(final TypeView<?> type) {
        final ElementAnnotationsIntrospector annotations = type.annotations();

        for (final NamedQuery namedQuery : annotations.all(NamedQuery.class)) {
            final ComponentNamedQueryContext queryContext = new ComponentNamedQueryContext(namedQuery.name(), false, namedQuery.query(), type);
            this.add(queryContext);
        }

        for (final NamedNativeQuery namedNativeQuery : annotations.all(NamedNativeQuery.class)) {
            final ComponentNamedQueryContext queryContext = new ComponentNamedQueryContext(namedNativeQuery.name(), true, namedNativeQuery.query(), type);
            this.add(queryContext);
        }
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        for (final Entry<String, ComponentNamedQueryContext> entry : this.namedQueries.entrySet()) {
            collector.property(entry.getKey()).write(c -> entry.getValue().report(c));
        }
    }
}
