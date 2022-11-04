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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TypeReferenceLookupComponentLocator implements ComponentLocator, ContextCarrier {

    private final ApplicationContext applicationContext;
    private final Set<ComponentContainer> componentContainers = ConcurrentHashMap.newKeySet();

    public TypeReferenceLookupComponentLocator(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<ComponentContainer> containers() {
        if (this.componentContainers.isEmpty()) {
            this.applicationContext().environment().types(Component.class).stream()
                    .filter(type -> !type.isAnnotation()) // Filter activators
                    .map(type -> new ComponentContainerImpl(this.applicationContext(), type.type()))
                    .forEach(this.componentContainers::add);
        }
        return this.componentContainers;
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

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
