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

import java.util.function.Predicate;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class TypeReferenceLookupComponentRegistry implements ComponentRegistry {

    private final ApplicationEnvironment environment;
    private final Set<ComponentContainer<?>> componentContainers = ConcurrentHashMap.newKeySet();

    public TypeReferenceLookupComponentRegistry(ApplicationEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Collection<ComponentContainer<?>> containers() {
        if (this.componentContainers.isEmpty()) {
            this.environment.typeResolver().types(Component.class).stream()
                .filter(Predicate.not(TypeView::isAnnotation)) // Ensure stereotypes are not included
                .map(ComponentContainerImpl::new)
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
    public ApplicationContext applicationContext() {
        return this.environment.applicationContext();
    }
}
