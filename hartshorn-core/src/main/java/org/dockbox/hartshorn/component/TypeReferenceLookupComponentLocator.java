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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeReferenceLookupComponentLocator implements ComponentLocator {

    private static final Logger LOG = LoggerFactory.getLogger(TypeReferenceLookupComponentLocator.class);

    private final ApplicationContext applicationContext;
    private final Set<ComponentContainer<?>> componentContainers = ConcurrentHashMap.newKeySet();

    public TypeReferenceLookupComponentLocator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<ComponentContainer<?>> containers() {
        if (this.componentContainers.isEmpty()) {
            this.applicationContext().environment().types(Component.class).stream()
                    .filter(type -> !type.isAnnotation()) // Filter activators
                    .map(type -> new ComponentContainerImpl<>(this.applicationContext(), type.type()))
                    .forEach(this.componentContainers::add);
        }
        return this.componentContainers;
    }

    @Override
    public Collection<ComponentContainer<?>> containers(ComponentType componentType) {
        return this.containers().stream()
                .filter(container -> container.componentType() == componentType)
                .toList();
    }

    @Override
    public Option<ComponentContainer<?>> container(Class<?> type) {
        return Option.of(this.containers()
                .stream()
                .filter(container -> container.type().is(type))
                .findFirst()
        );
    }

    @Override
    public <T> void validate(ComponentKey<T> key) {
        Introspector introspector = this.applicationContext().environment().introspector();
        TypeView<T> contract = introspector.introspect(key.type());

        if (contract.annotations().has(Component.class) && this.container(contract.type()).absent()) {
            LOG.warn("Component key '%s' is annotated with @Component, but is not registered.".formatted(contract.qualifiedName()));
        }
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
