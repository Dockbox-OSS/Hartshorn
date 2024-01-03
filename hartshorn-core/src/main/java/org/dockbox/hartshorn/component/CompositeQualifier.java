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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CompositeQualifier implements Reportable {

    private final Map<Class<?>, QualifierKey<?>> qualifiers = new HashMap<>();

    public CompositeQualifier add(QualifierKey<?> qualifier) {
        this.qualifiers.put(qualifier.type(), qualifier);
        return this;
    }

    public CompositeQualifier addAll(QualifierKey<?>... qualifiers) {
        this.addAll(Set.of(qualifiers));
        return this;
    }

    public CompositeQualifier addAll(Set<QualifierKey<?>> qualifiers) {
        for (QualifierKey<?> qualifier : qualifiers) {
            this.add(qualifier);
        }
        return this;
    }

    public void addAll(CompositeQualifier qualifier) {
        this.addAll(qualifier.qualifiers());
    }

    public Set<QualifierKey<?>> qualifiers() {
        return Set.copyOf(this.qualifiers.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        CompositeQualifier that = (CompositeQualifier) o;
        return Objects.equals(this.qualifiers, that.qualifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.qualifiers);
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        String[] qualifierNames = this.qualifiers.keySet().stream()
                .map(Class::getSimpleName)
                .toArray(String[]::new);
        collector.property("qualifiers").write(qualifierNames);
    }

    @Override
    public String toString() {
        String qualifierString = StringUtilities.join(", ", this.qualifiers.values(), QualifierKey::toString);
        return "%s".formatted(qualifierString);
    }

    public boolean isEmpty() {
        return this.qualifiers == null || this.qualifiers.isEmpty();
    }
}
