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

package org.dockbox.hartshorn.component;

import java.util.Objects;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A key that can be used to identify a {@link Scope}. This contains the required metadata to
 * identify a scope, and can be used to manage scoped components in a {@link ScopeAwareComponentProvider}.
 *
 * <p>Scope keys contain a {@link ParameterizableType} that describes the type of the scope. This type can
 * be parameterized. Therefore, key instances differentiate between e.g. {@code TypeScope<String>} and {@code TypeScope<Integer>}.
 *
 * <p>Keys are always immutable.
 *
 * @see ScopeAwareComponentProvider
 * @see Scope
 *
 * @param <T> the type of the scope
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ScopeKey<T extends Scope> {

    private final ParameterizableType<T> scopeType;

    protected ScopeKey(ParameterizableType<T> scopeType) {
        this.scopeType = scopeType;
    }

    /**
     * Returns the name of the scope type. This is the simple name of the type, without any package
     * information, but including any type parameters.
     *
     * @return the name of the scope type
     */
    public String name() {
        return this.scopeType.toString();
    }

    /**
     * Returns the type of the scope. This contains the full type information, including any type
     * parameters.
     *
     * @return the type of the scope
     */
    public ParameterizableType<T> scopeType() {
        return this.scopeType;
    }

    /**
     * Creates a new {@link ScopeKey} for the given type. This is a convenience method that allows for
     * the creation of a key without having to specify type parameters.
     *
     * @param scopeType the type of the scope
     * @return a new {@link ScopeKey} instance
     * @param <T> the type of the scope
     */
    public static <T extends Scope> ScopeKey<T> of(Class<T> scopeType) {
        return new ScopeKey<>(new ParameterizableType<>(scopeType));
    }

    /**
     * Creates a new {@link ScopeKey} for the given type. This is a convenience method that allows for
     * the creation of a key from existing type metadata.
     *
     * @param scopeType the type of the scope
     * @return a new {@link ScopeKey} instance
     * @param <T> the type of the scope
     */
    public static <T extends Scope> ScopeKey<T> of(TypeView<T> scopeType) {
        return new ScopeKey<>(new ParameterizableType<>(scopeType));
    }

    /**
     * Creates a new {@link ScopeKey} for the given type. This is a convenience method that allows for
     * the creation of a key from existing type metadata.
     *
     * @param scopeType the type of the scope
     * @return a new {@link ScopeKey} instance
     * @param <T> the type of the scope
     */
    public static <T extends Scope> ScopeKey<T> of(ParameterizableType<T> scopeType) {
        return new ScopeKey<>(scopeType);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ScopeKey<?> scopeKey)) {
            return false;
        }
        return Objects.equals(this.scopeType, scopeKey.scopeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.scopeType);
    }
}
