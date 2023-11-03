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

package org.dockbox.hartshorn.inject;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.collections.StandardMultiMap;

public class DependencyMap extends StandardMultiMap<DependencyResolutionType, ComponentKey<?>> {

    private DependencyMap() {
        // Use static factory method
    }

    @Override
    protected Map<DependencyResolutionType, Collection<ComponentKey<?>>> createEmptyMap() {
        return new EnumMap<>(DependencyResolutionType.class);
    }

    @Override
    protected Collection<ComponentKey<?>> createEmptyCollection() {
        return new HashSet<>();
    }

    public static DependencyMap create() {
        return new DependencyMap();
    }

    public DependencyMap immediate(final ComponentKey<?> key) {
        this.put(DependencyResolutionType.IMMEDIATE, key);
        return this;
    }

    public DependencyMap immediate(final Collection<ComponentKey<?>> keys) {
        this.putAll(DependencyResolutionType.IMMEDIATE, keys);
        return this;
    }

    public DependencyMap delayed(final ComponentKey<?> key) {
        this.put(DependencyResolutionType.DELAYED, key);
        return this;
    }

    public DependencyMap delayed(final Collection<ComponentKey<?>> keys) {
        this.putAll(DependencyResolutionType.DELAYED, keys);
        return this;
    }
}
