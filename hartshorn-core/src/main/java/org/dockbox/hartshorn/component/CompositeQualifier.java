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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.dockbox.hartshorn.inject.Named;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;

/**
 * A {@link CompositeQualifier} is a collection of {@link QualifierKey}s. It is used to qualify a {@link ComponentKey}
 * with zero or more qualifiers. While multiple {@link QualifierKey}s can be used to qualify a {@link ComponentKey},
 * only one {@link QualifierKey} of a specific type can be used. For example, a {@link ComponentKey} can be qualified
 * with one {@link QualifierKey} of type {@link Named}, but not with two or more.
 *
 * @see QualifierKey
 * @see ComponentKey
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class CompositeQualifier implements Reportable {

    private final Map<Class<?>, QualifierKey<?>> qualifiers = new HashMap<>();

    /**
     * Adds the provided qualifier to this {@link CompositeQualifier}. If a qualifier of the same type already exists,
     * it is replaced by the provided qualifier.
     *
     * @param qualifier The qualifier to add.
     * @return This {@link CompositeQualifier} instance.
     */
    public CompositeQualifier add(QualifierKey<?> qualifier) {
        this.qualifiers.put(qualifier.type(), qualifier);
        return this;
    }

    /**
     * Adds the provided qualifiers to this {@link CompositeQualifier}. If a qualifier of the same type already exists,
     * it is replaced by the provided qualifier.
     *
     * @param qualifiers The qualifiers to add.
     * @return This {@link CompositeQualifier} instance.
     */
    public CompositeQualifier addAll(QualifierKey<?>... qualifiers) {
        this.addAll(Set.of(qualifiers));
        return this;
    }

    /**
     * Adds the provided qualifiers to this {@link CompositeQualifier}. If a qualifier of the same type already exists,
     * it is replaced by the provided qualifier.
     *
     * @param qualifiers The qualifiers to add.
     * @return This {@link CompositeQualifier} instance.
     */
    public CompositeQualifier addAll(Set<QualifierKey<?>> qualifiers) {
        for (QualifierKey<?> qualifier : qualifiers) {
            this.add(qualifier);
        }
        return this;
    }

    /**
     * Adds the qualifiers of the provided {@link CompositeQualifier} to this {@link CompositeQualifier}. If a qualifier
     * of the same type already exists, it is replaced by the provided qualifier.
     *
     * @param qualifier The qualifier to add.
     */
    public void addAll(CompositeQualifier qualifier) {
        this.addAll(qualifier.qualifiers());
    }

    /**
     * Returns all qualifiers of this {@link CompositeQualifier}. If no qualifiers are present, an empty set is
     * returned.
     *
     * @return All qualifiers of this {@link CompositeQualifier}.
     */
    public Set<QualifierKey<?>> qualifiers() {
        return Set.copyOf(this.qualifiers.values());
    }

    public boolean isEmpty() {
        return this.qualifiers.isEmpty();
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
        collector.property("qualifiers").writeStrings(qualifierNames);
    }

    @Override
    public String toString() {
        String qualifierString = StringUtilities.join(", ", this.qualifiers.values(), QualifierKey::toString);
        return "%s".formatted(qualifierString);
    }
}
