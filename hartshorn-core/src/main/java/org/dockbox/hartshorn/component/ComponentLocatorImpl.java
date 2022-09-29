/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ObservingApplicationContext;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedHashSetMultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;

public class ComponentLocatorImpl implements ComponentLocator {

    private final MultiMap<String, ComponentContainer> cache = new SynchronizedHashSetMultiMap<>();
    private final ApplicationContext applicationContext;
    private final ConditionMatcher conditionMatcher;

    public ComponentLocatorImpl(final InitializingContext context) {
        this.applicationContext = context.applicationContext();
        this.conditionMatcher = context.conditionMatcher();
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public void register(final String prefix) {
        if (this.cache.containsKey(prefix)) return;

        this.applicationContext().log().debug("Registering prefix '" + prefix + "' for component locating");

        final long start = System.currentTimeMillis();

        final List<TypeView<?>> newComponentTypes = this.applicationContext().environment()
                .types(prefix, Component.class, false)
                .stream()
                .filter(type -> this.cache.allValues().stream().noneMatch(container -> container.type().equals(type)))
                .toList();

        final List<ComponentContainer> newComponentContainers = newComponentTypes.stream()
                .map(type -> new ComponentContainerImpl(this.applicationContext(), type.type()))
                .filter(container -> !container.type().isAnnotation()) // Exclude extended annotations
                .map(ComponentContainer.class::cast).toList();

        final List<ComponentContainer> filteredComponentContainers = newComponentContainers.stream()
                .filter(container -> this.conditionMatcher.match(container.type()))
                .toList();

        final long duration = System.currentTimeMillis() - start;
        this.applicationContext().log().info("Located %d components with prefix %s in %dms".formatted(filteredComponentContainers.size(), prefix, duration));

        filteredComponentContainers.forEach(container -> {
            if (this.applicationContext() instanceof ObservingApplicationContext context) {
                context.componentAdded(container);
            }
            this.cache.put(prefix, container);
        });
    }

    @Override
    public void register(final Class<?> type) {
        if (this.container(type).absent()) {
            final ComponentContainer container = new ComponentContainerImpl(this.applicationContext(), type);
            if (!container.type().isAnnotation() && this.conditionMatcher.match(container.type())) {
                this.cache.put(type.getPackageName(), container);
                if (this.applicationContext() instanceof ObservingApplicationContext context) {
                    context.componentAdded(container);
                }
            }
        }
    }

    @Override
    public Collection<ComponentContainer> containers() {
        return this.cache.entrySet().stream().flatMap(a -> a.getValue().stream()).toList();
    }

    @Override
    public Collection<ComponentContainer> containers(final ComponentType componentType) {
        return this.containers().stream()
                .filter(container -> container.componentType() == componentType)
                .toList();
    }

    @Override
    public Result<ComponentContainer> container(final Class<?> type) {
        return Result.of(this.containers()
                .stream()
                .filter(container -> container.type().is(type))
                .findFirst()
        );
    }

    @Override
    public <T> void validate(final Key<T> key) {
        final Introspector introspector = this.applicationContext().environment();
        final TypeView<T> contract = introspector.introspect(key.type());

        // Skip introspection types, to avoid infinite recursion. Introspectors are utility classes,
        // and are never registered as components.
        if (contract.isDeclaredIn(Introspector.class.getPackageName())) return;

        if (contract.annotations().has(Component.class) && this.container(contract.type()).absent()) {
            this.applicationContext().log().warn("Component key '%s' is annotated with @Component, but is not registered.".formatted(contract.qualifiedName()));
        }
    }
}
