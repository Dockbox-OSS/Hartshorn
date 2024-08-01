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

import org.dockbox.hartshorn.util.introspect.ParameterizableType;

import java.util.Objects;

/**
 * A {@link ScopeAdapter} is a wrapper around a non-{@link Scope} object that allows for the object to be used as a
 * {@link Scope}. This is useful when a scope is based on certain properties of an object, but the object itself is not
 * a {@link Scope}. For example, a {@link Scope} can be based on an active HTTP request.
 *
 * @param <T> the type of the adaptee
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ScopeAdapter<T> implements Scope {

    private final T adaptee;
    private final ParameterizableType adapteeType;

    protected ScopeAdapter(T adaptee, ParameterizableType adapteeType) {
        this.adaptee = adaptee;
        this.adapteeType = adapteeType;
    }

    /**
     * Returns the adaptee of this adapter.
     *
     * @return the adaptee
     */
    public T adaptee() {
        return this.adaptee;
    }

    /**
     * Returns the parameterized type of the adaptee.
     *
     * @return the parameterized type of the adaptee
     */
    public ParameterizableType adapteeType() {
        return this.adapteeType;
    }

    /**
     * Creates a new {@link ScopeAdapter} for the given adaptee.
     *
     * @param adaptee the adaptee
     * @param type the parameterized type of the adaptee
     * @param <T> the type of the adaptee
     *
     * @return the new adapter
     */
    public static <T> ScopeAdapter<T> of(T adaptee, ParameterizableType type) {
        return new ScopeAdapter<>(adaptee, type);
    }

    /**
     * Creates a new {@link ScopeAdapter} for the given adaptee.
     *
     * @param adaptee the adaptee
     * @param <T> the type of the adaptee
     *
     * @return the new adapter
     */
    public static <T> ScopeAdapter<T> of(T adaptee) {
        return new ScopeAdapter<>(adaptee, ParameterizableType.create(adaptee.getClass()));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ScopeAdapter<?> adapter = (ScopeAdapter<?>) other;
        return this.adaptee.equals(adapter.adaptee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.adaptee);
    }

    @Override
    public ScopeAdapterKey installableScopeType() {
        return ScopeAdapterKey.of(this);
    }
}
