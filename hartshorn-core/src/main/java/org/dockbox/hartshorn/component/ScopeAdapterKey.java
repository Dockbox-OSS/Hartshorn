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

import org.dockbox.hartshorn.util.TypeUtils;

public class ScopeAdapterKey<T> extends ScopeKey<ScopeAdapter<T>> {

    private final Class<T> adapteeType;

    protected ScopeAdapterKey(Class<ScopeAdapter<T>> type, Class<T> adapteeType) {
        super(type);
        this.adapteeType = adapteeType;
    }

    public Class<T> adapteeType() {
        return adapteeType;
    }

    public static <T> ScopeAdapterKey<T> of(ScopeAdapter<T> adapter) {
        Class<ScopeAdapter<T>> adapterType = TypeUtils.adjustWildcards(adapter.getClass(), Class.class);
        Class<T> adapteeType = TypeUtils.adjustWildcards(adapter.adaptee().getClass(), Class.class);
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
        return Objects.equals(adapteeType, that.adapteeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adapteeType);
    }
}
