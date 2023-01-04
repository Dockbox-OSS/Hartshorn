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

public class ScopeAdapter<T> implements Scope {

    private final T adaptee;

    private ScopeAdapter(final T adaptee) {
        this.adaptee = adaptee;
    }

    public T adaptee() {
        return this.adaptee;
    }

    public static <T> ScopeAdapter<T> of(final T adaptee) {
        return new ScopeAdapter<>(adaptee);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ScopeAdapter<?> that = (ScopeAdapter<?>) o;
        return this.adaptee.equals(that.adaptee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.adaptee);
    }

    @Override
    public Class<? extends Scope> installableScopeType() {
        return Scope.class;
    }
}
