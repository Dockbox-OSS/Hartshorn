/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.component;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectorConfiguration;
import org.dockbox.hartshorn.inject.InjectorUtilities;
import org.dockbox.hartshorn.inject.SimpleComponentKeyMatcher;
import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.component.AnnotatedComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.environment.EnvironmentTypeResolver;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.stream.CollectorUtilities;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

/**
 * {@link ComponentRegistry} implementation which recognizes classes annotated with
 * {@link Component} (or compatible stereotypes) as managed components. Classes are resolved through
 * the given {@link EnvironmentTypeResolver}, which is typically derived from the active
 * {@link ApplicationEnvironment} through {@link ApplicationEnvironment#typeResolver()}. Additional
 * containers can be added to this registry through
 * {@link #addCustomContainer(ComponentContainer)}.
 *
 * <p>Component resolution is cached, meaning this registry expects all required type resolution
 * rules to be configured
 * before {@link #containers()} is first called. Note that custom component containers can always be
 * added, and will be taken into account when resolving all or specific component containers.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class TypeReferenceLookupComponentRegistry implements ComponentRegistry {

    private final Set<ComponentContainer<?>> containers =
        new ConcurrentSkipListSet<>(ComponentContainer.COMPARE_BY_ID);
    private final EnvironmentTypeResolver typeResolver;
    private final InjectorConfiguration configuration;
    private boolean environmentTypesResolved = false;

    public TypeReferenceLookupComponentRegistry(
        EnvironmentTypeResolver typeResolver,
        InjectorConfiguration configuration
    ) {
        this.typeResolver = typeResolver;
        this.configuration = configuration;
    }

    /**
     * Register the given container to the current registry
     *
     * @param container the container to register
     *
     * @return whether the registry already contained a container with the same ID
     */
    @Override
    public boolean addCustomContainer(ComponentContainer<?> container) {
        return this.withContainerCache(containers -> safeAddContainer(containers, container));
    }

    private boolean safeAddContainer(
        Set<ComponentContainer<?>> containers,
        ComponentContainer<?> container
    ) {
        Option<ComponentContainer<?>> existingContainer = containers.stream()
            .filter(c -> c.id().equals(container.id()))
            .collect(CollectorUtilities.toOption());
        if (existingContainer.present()) {
            ComponentContainer<?> firstContainer = existingContainer.get();
            if (firstContainer.type().is(container.type().type())) {
                // If the existing container is of the same type, we can safely replace it
                containers.remove(firstContainer);
            }
            else {
                throw new IllegalStateException(
                    ("A container with id '%s' already exists "
                        + "(existing: %s, attempted to add: %s). "
                        + "Define a unique ID for either or both of these components.").formatted(
                        container.id(),
                        firstContainer.type(),
                        container.type()
                    ));
            }
        }
        return containers.add(container);
    }

    @Override
    public Collection<ComponentContainer<?>> containers() {
        return List.copyOf(this.withContainerCache(Function.identity()));
    }

    @Override
    public Option<ComponentContainer<?>> container(Class<?> type) {
        return this.withContainerCache(containers -> containers.stream()
            .filter(container -> container.type().is(type))
            .collect(CollectorUtilities.toOption()));
    }

    @Override
    public Option<ComponentContainer<?>> container(ComponentKey<?> key) {
        if (!key.qualifier().isEmpty()) {
            // Qualifiers are not supported in this registry
            return Option.empty();
        }
        else if (InjectorUtilities.isStrict(key, this.configuration)) {
            return container(key.type());
        }
        else {
            return this.withContainerCache(containers -> containers.stream()
                .filter(container -> SimpleComponentKeyMatcher.INSTANCE.matches(key,
                    ComponentKey.of(container.type())))
                .collect(CollectorUtilities.toOption()));
        }
    }

    private <T> T withContainerCache(Function<Set<ComponentContainer<?>>, T> operator) {
        this.initializeCacheIfEmpty();
        return operator.apply(this.containers);
    }

    private void initializeCacheIfEmpty() {
        // Don't check if the cache is empty, as that would still be a valid state if no
        // components are registered.
        if (!this.environmentTypesResolved) {
            this.environmentTypesResolved = true;
            this.typeResolver.types(Component.class).stream()
                // Filter out component stereotypes (annotation types)
                .filter(type -> !AnnotationUtilities.isStereotypeOf(type.type(), Component.class))
                .map(AnnotatedComponentContainer::new)
                .forEach(container -> this.safeAddContainer(this.containers, container));
        }
    }
}
