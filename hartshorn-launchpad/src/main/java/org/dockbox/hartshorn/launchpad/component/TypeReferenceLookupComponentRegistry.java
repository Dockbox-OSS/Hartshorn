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

package org.dockbox.hartshorn.launchpad.component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.AnnotatedComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.launchpad.environment.EnvironmentTypeResolver;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class TypeReferenceLookupComponentRegistry implements ComponentRegistry {

    private final EnvironmentTypeResolver typeResolver;
    private final Set<ComponentContainer<?>> componentContainers = ConcurrentHashMap.newKeySet();
    private final Set<ComponentContainer<?>> customContainers = ConcurrentHashMap.newKeySet();

    public TypeReferenceLookupComponentRegistry(EnvironmentTypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public void addCustomContainer(ComponentContainer<?> container) {
        this.customContainers.add(container);
    }

    @Override
    public Collection<ComponentContainer<?>> containers() {
        if (this.componentContainers.isEmpty()) {
            this.typeResolver.types(Component.class).stream()
                .filter(Predicate.not(TypeView::isAnnotation)) // Ensure stereotypes are not included
                .map(AnnotatedComponentContainer::new)
                .forEach(this.componentContainers::add);
        }
        return CollectionUtilities.merge(this.componentContainers, this.customContainers);
    }

    @Override
    public Option<ComponentContainer<?>> container(Class<?> type) {
        return Option.of(this.containers()
                .stream()
                .filter(container -> container.type().is(type))
                .findFirst()
        );
    }
}
