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

public class ScopeAdapter<T> implements Scope {

    private final T adaptee;
    private final ParameterizableType adapteeType;

    protected ScopeAdapter(T adaptee, ParameterizableType adapteeType) {
        this.adaptee = adaptee;
        this.adapteeType = adapteeType;
    }

    public T adaptee() {
        return this.adaptee;
    }

    public ParameterizableType adapteeType() {
        return this.adapteeType;
    }

    public static <T> ScopeAdapter<T> of(T adaptee, ParameterizableType type) {
        return new ScopeAdapter<>(adaptee, type);
    }

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
