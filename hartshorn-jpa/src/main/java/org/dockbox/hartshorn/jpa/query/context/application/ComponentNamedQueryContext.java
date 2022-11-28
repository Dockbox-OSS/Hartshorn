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

package org.dockbox.hartshorn.jpa.query.context.application;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.Entity;

public class ComponentNamedQueryContext extends DefaultContext implements Reportable {

    private final String name;
    private final boolean nativeQuery;
    private final String query;
    private final TypeView<?> declaredBy;

    private boolean automaticFlush = true;
    private boolean automaticClear = false;

    public ComponentNamedQueryContext(final String name, final boolean nativeQuery, final String query,
                                      final TypeView<?> declaredBy) {
        this.name = name;
        this.nativeQuery = nativeQuery;
        this.query = query;
        this.declaredBy = declaredBy;
    }

    public String name() {
        return this.name;
    }

    public boolean nativeQuery() {
        return this.nativeQuery;
    }

    public String query() {
        return this.query;
    }

    public TypeView<?> declaredBy() {
        return this.declaredBy;
    }

    public boolean automaticFlush() {
        return this.automaticFlush;
    }

    public ComponentNamedQueryContext automaticFlush(final boolean automaticFlush) {
        this.automaticFlush = automaticFlush;
        return this;
    }

    public boolean automaticClear() {
        return this.automaticClear;
    }

    public ComponentNamedQueryContext automaticClear(final boolean automaticClear) {
        this.automaticClear = automaticClear;
        return this;
    }

    public boolean isEntityDeclaration() {
        return this.declaredBy.annotations().has(Entity.class);
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name);
        collector.property("native").write(this.nativeQuery);
        collector.property("query").write(this.query);
        collector.property("declaredBy").write(this.declaredBy.qualifiedName());
        collector.property("automaticFlush").write(this.automaticFlush);
        collector.property("automaticClear").write(this.automaticClear);
    }
}
