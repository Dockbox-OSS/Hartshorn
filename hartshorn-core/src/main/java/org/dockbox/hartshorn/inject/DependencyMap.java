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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.collections.StandardMultiMap;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A map that can be used to define dependencies for a component. Dependencies are grouped by
 * their {@link DependencyResolutionType}. If multiple dependencies are defined for the same
 * resolution type, they are all added to the same collection.
 *
 * @see DependencyResolutionType
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class DependencyMap extends StandardMultiMap<DependencyResolutionType, ComponentKey<?>> {

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

    /**
     * Creates a new empty dependency map.
     *
     * @return the new dependency map
     */
    public static DependencyMap create() {
        return new DependencyMap();
    }

    /**
     * Adds the given component key as a dependency which requires immediate resolution.
     *
     * @param key the component key to add
     * @return the dependency map
     *
     * @see DependencyResolutionType#IMMEDIATE
     */
    public DependencyMap immediate(ComponentKey<?> key) {
        this.put(DependencyResolutionType.IMMEDIATE, key);
        return this;
    }

    /**
     * Adds the given component keys as dependencies which require immediate resolution.
     *
     * @param keys the component keys to add
     * @return the dependency map
     *
     * @see DependencyResolutionType#IMMEDIATE
     */
    public DependencyMap immediate(Collection<ComponentKey<?>> keys) {
        this.putAll(DependencyResolutionType.IMMEDIATE, keys);
        return this;
    }

    /**
     * Adds the given component key as a dependency which may be resolved at a later time.
     *
     * @param key the component key to add
     * @return the dependency map
     *
     * @see DependencyResolutionType#DELAYED
     */
    public DependencyMap delayed(ComponentKey<?> key) {
        this.put(DependencyResolutionType.DELAYED, key);
        return this;
    }

    /**
     * Adds the given component keys as dependencies which may be resolved at a later time.
     *
     * @param keys the component keys to add
     * @return the dependency map
     *
     * @see DependencyResolutionType#DELAYED
     */
    public DependencyMap delayed(Collection<ComponentKey<?>> keys) {
        this.putAll(DependencyResolutionType.DELAYED, keys);
        return this;
    }
}
