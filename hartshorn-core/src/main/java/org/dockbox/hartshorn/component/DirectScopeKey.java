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
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Objects;

/**
 * Simple implementation of a {@link ScopeKey}, to be used for direct implementations of
 * {@link Scope}. For {@link ScopeAdapter}s, use {@link ScopeAdapterKey} instead.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class DirectScopeKey implements ScopeKey {

    private final ParameterizableType scopeType;

    protected DirectScopeKey(ParameterizableType scopeType) {
        if (!Scope.class.isAssignableFrom(scopeType.type())) {
            throw new IllegalArgumentException("Scope type must be a subtype of Scope");
        }
        this.scopeType = scopeType;
    }

    @Override
    public String name() {
        return this.scopeType.toString();
    }

    @Override
    public ParameterizableType scopeType() {
        return this.scopeType;
    }

    /**
     * Creates a new {@link DirectScopeKey} for the given type. This is a convenience method that allows for
     * the creation of a key without having to specify type parameters.
     *
     * @param scopeType the type of the scope
     * @return a new {@link DirectScopeKey} instance
     * @param <T> the type of the scope
     */
    public static <T extends Scope> DirectScopeKey of(Class<T> scopeType) {
        return new DirectScopeKey(ParameterizableType.create(scopeType));
    }

    /**
     * Creates a new {@link DirectScopeKey} for the given type. This is a convenience method that allows for
     * the creation of a key from existing type metadata.
     *
     * @param scopeType the type of the scope
     * @return a new {@link DirectScopeKey} instance
     * @param <T> the type of the scope
     */
    public static <T extends Scope> DirectScopeKey of(TypeView<T> scopeType) {
        return new DirectScopeKey(ParameterizableType.create(scopeType));
    }

    /**
     * Creates a new {@link DirectScopeKey} for the given type. This is a convenience method that allows for
     * the creation of a key from existing type metadata.
     *
     * @param scopeType the type of the scope
     * @return a new {@link DirectScopeKey} instance
     */
    public static DirectScopeKey of(ParameterizableType scopeType) {
        // Note that type is not checked here, as constructor already does this.
        return new DirectScopeKey(scopeType);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof DirectScopeKey scopeKey)) {
            return false;
        }
        return Objects.equals(this.scopeType, scopeKey.scopeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.scopeType);
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("type").writeDelegate(this.scopeType);
    }

    @Override
    public String toString() {
        return "DirectScopeKey{" +
            "scopeType=" + this.scopeType +
            '}';
    }
}
