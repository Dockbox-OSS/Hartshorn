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

import java.util.List;
import java.util.Objects;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

/**
 * Specialized {@link ScopeKey} for {@link ScopeAdapter} instances. This tracks both the adaptee type
 * and the scope adapter type.
 *
 * @param <T> the adaptee type
 *
 * @see ScopeAdapter
 * @see ScopeKey
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ScopeAdapterKey<T> extends ScopeKey<ScopeAdapter<T>> {

    private final ParameterizableType<T> adapteeType;

    protected ScopeAdapterKey(ParameterizableType<ScopeAdapter<T>> type, ParameterizableType<T> adapteeType) {
        super(type);
        this.adapteeType = adapteeType;
    }

    /**
     * Returns the type of the adaptee. This contains the full type information, including any type
     * parameters. This is not the type of the scope itself, but of the instance that is wrapped by
     * the adapter.
     *
     * @return the type of the adaptee
     */
    public ParameterizableType<T> adapteeType() {
        return this.adapteeType;
    }

    /**
     * Creates a new key for the given adapter. The adaptee type is derived from the adapter.
     *
     * @param adapter the adapter to create a key for
     * @return the key
     * @param <T> the adaptee type
     */
    public static <T> ScopeAdapterKey<T> of(ScopeAdapter<T> adapter) {
        ParameterizableType<T> adapteeType = adapter.adapteeType();
        ParameterizableType<ScopeAdapter<T>> adapterType = TypeUtils.adjustWildcards(
            new ParameterizableType<>(ScopeAdapter.class, List.of(adapteeType)),
            ParameterizableType.class
        );
        return new ScopeAdapterKey<>(adapterType, adapteeType);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ScopeAdapterKey<?> that)) {
            return false;
        }
        if(!super.equals(object)) {
            return false;
        }
        return Objects.equals(this.adapteeType, that.adapteeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.adapteeType);
    }
}
