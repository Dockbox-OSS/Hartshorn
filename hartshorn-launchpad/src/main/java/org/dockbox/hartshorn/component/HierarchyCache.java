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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentKeyView;
import org.dockbox.hartshorn.inject.binding.AbstractBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.inject.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.collection.ImmutableCompositeBindingHierarchy;
import org.dockbox.hartshorn.inject.provider.HierarchicalComponentProvider;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.NavigableMultiMap;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class HierarchyCache {

    private final transient Map<ComponentKeyView<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final HierarchicalComponentProvider globalProvider;
    private final HierarchicalComponentProvider owner;

    public HierarchyCache(
            ApplicationContext applicationContext,
            HierarchicalComponentProvider globalProvider,
            HierarchicalComponentProvider owner) {
        this.applicationContext = applicationContext;
        this.globalProvider = globalProvider;
        this.owner = owner;
    }

    public void put(BindingHierarchy<?> hierarchy) {
        this.hierarchies.put(hierarchy.key().view(), hierarchy);
    }

    public <T> void put(ComponentKeyView<T> view, BindingHierarchy<T> updated) {
        this.hierarchies.put(view, updated);
    }

    public Set<BindingHierarchy<?>> hierarchies() {
        return Set.copyOf(this.hierarchies.values());
    }

    public <T> BindingHierarchy<?> getOrComputeHierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        ComponentKeyView<T> view = key.view();
        if (this.hierarchies.containsKey(view)) {
            return this.hierarchies.get(view);
        }
        else {
            return this.computeHierarchy(key, permitFallbackResolution);
        }
    }

    @NonNull
    private <T> BindingHierarchy<?> computeHierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        BindingHierarchy<?> hierarchy = this.tryCreateHierarchy(key, permitFallbackResolution);

        return Objects.requireNonNullElseGet(hierarchy, () -> {
            // If we don't have an explicit hierarchy on the key, we can try to use the hierarchy of
            // the application context. This is useful for components that are not explicitly scoped,
            // but are still accessed through a scope.
            if(permitFallbackResolution && this.globalProvider != this.owner) {
                return this.globalProvider.hierarchy(key);
            }
            return new NativePrunableBindingHierarchy<>(key);
        });
    }

    @Nullable
    private <T> BindingHierarchy<?> tryCreateHierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        BindingHierarchy<?> hierarchy;
        if(this.isStrict(key)) {
            hierarchy = this.createHierarchy(key);
            if (hierarchy != null) {
                this.hierarchies.put(key.view(), hierarchy);
            }
        }
        else if (permitFallbackResolution) {
            // Don't bind this hierarchy, as it's a loose match. If the configuration changes, the loose
            // match may not be valid anymore, so we don't want to cache it.
            hierarchy = this.looseLookupHierarchy(key);
        }
        else if (this.isCollectionComponentKey(key)) {
            hierarchy = new CollectionBindingHierarchy<>(TypeUtils.adjustWildcards(key, ComponentKey.class));
        }
        else {
            hierarchy = null;
        }
        return hierarchy;
    }

    protected boolean isStrict(ComponentKey<?> key) {
        Tristate strict = key.strict();
        if (strict == Tristate.UNDEFINED) {
            return this.applicationContext.environment().isStrictMode();
        }
        else {
            return strict.booleanValue();
        }
    }

    @Nullable
    private <T> AbstractBindingHierarchy<?> createHierarchy(ComponentKey<T> key) {
        if (this.isCollectionComponentKey(key)) {
            return new CollectionBindingHierarchy<>(TypeUtils.adjustWildcards(key, ComponentKey.class));
        }
        else {
            return null;
        }
    }

    @Nullable
    private <T> BindingHierarchy<?> looseLookupHierarchy(ComponentKey<T> key) {
        Set<ComponentKeyView<?>> hierarchyKeys = this.hierarchies.keySet();
        Set<ComponentKeyView<?>> compatibleKeys = hierarchyKeys.stream()
                .filter(hierarchyKey -> this.isCompatible(key, hierarchyKey))
                .collect(Collectors.toSet());

        if (this.isCollectionComponentKey(key)) {
            return this.composeCollectionHierarchy(TypeUtils.adjustWildcards(key, ComponentKey.class), compatibleKeys);
        }
        else {
            if (compatibleKeys.size() == 1) {
                ComponentKeyView<?> compatibleKey = CollectionUtilities.first(compatibleKeys);
                return this.hierarchies.get(compatibleKey);
            }
            else {
                // Acceptable, as long as there is a single highest priority binding. If multiple match, it's an error.
                return this.lookupHighestPriorityHierarchy(key, compatibleKeys);
            }
        }
    }

    private boolean isCollectionComponentKey(ComponentKey<?> key) {
        return ComponentCollection.class.isAssignableFrom(key.type());
    }

    @Nullable
    private BindingHierarchy<?> lookupHighestPriorityHierarchy(ComponentKey<?> key, Set<ComponentKeyView<?>> compatibleKeys) {
        Set<BindingHierarchy<?>> compatibleHierarchies = compatibleKeys.stream()
                .map(this.hierarchies::get)
                .collect(Collectors.toSet());

        // Track entire hierarchy, so potential duplicate top-priority hierarchies can be reported
        NavigableMultiMap<Integer, BindingHierarchy<?>> providers = new ConcurrentSetTreeMultiMap<>();
        for (BindingHierarchy<?> compatibleHierarchy : compatibleHierarchies) {
            int highestPriority = compatibleHierarchy.highestPriority();
            compatibleHierarchy.get(highestPriority).peek(provider -> {
                providers.put(highestPriority, compatibleHierarchy);
            });
        }
        Collection<BindingHierarchy<?>> highestPriority = providers.lastEntry();
        if (highestPriority.size() > 1) {
            Set<ComponentKey<?>> foundKeys = highestPriority.stream()
                    .map(BindingHierarchy::key)
                    .collect(Collectors.toSet());
            throw new AmbiguousComponentException(key, foundKeys);
        }
        return CollectionUtilities.first(highestPriority);
    }

    private boolean isCompatible(ComponentKey<?> key, ComponentKeyView<?> other) {
        ParameterizableType originType = key.parameterizedType();
        ParameterizableType targetType = other.type();
        return this.isCompatible(originType, targetType);
    }

    private boolean isCompatible(ParameterizableType originType, ParameterizableType targetType) {
        if (!originType.type().isAssignableFrom(targetType.type())) {
            return false;
        }
        List<ParameterizableType> originalParameters = originType.parameters();
        List<ParameterizableType> targetParameters = targetType.parameters();
        if (originalParameters.size() != targetParameters.size()) {
            return false;
        }
        for (int i = 0; i < originalParameters.size(); i++) {
            ParameterizableType originalParameter = originalParameters.get(i);
            ParameterizableType targetParameter = targetParameters.get(i);
            if (!this.isCompatible(originalParameter, targetParameter)) {
                return false;
            }
        }
        return true;
    }

    private <T> BindingHierarchy<?> composeCollectionHierarchy(ComponentKey<ComponentCollection<T>> key, Set<ComponentKeyView<?>> compatibleKeys) {
        Set<CollectionBindingHierarchy<?>> hierarchies = new HashSet<>();
        for (ComponentKeyView<?> compatibleKey : compatibleKeys) {
            BindingHierarchy<?> hierarchy = this.hierarchies.get(compatibleKey);
            if (hierarchy instanceof CollectionBindingHierarchy<?> collectionBindingHierarchy) {
                hierarchies.add(collectionBindingHierarchy);
            }
            else {
                throw new IllegalStateException("Found incompatible hierarchy for key " + compatibleKey +". Expected CollectionBindingHierarchy, but found " + hierarchy.getClass().getSimpleName());
            }
        }
        return new ImmutableCompositeBindingHierarchy<>(key, TypeUtils.adjustWildcards(hierarchies, Collection.class));
    }
}
