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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentKey.Builder;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Named;

public abstract class AbstractExecutableElementDependencyResolver implements DependencyResolver {

    @Override
    public Set<DependencyContext<?>> resolve(final Collection<ComponentContainer> containers, final ApplicationContext applicationContext) throws DependencyResolutionException {
        final Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        for (final ComponentContainer componentContainer : containers) {
            dependencyContexts.addAll(this.resolveSingle(componentContainer, applicationContext));
        }
        return dependencyContexts;
    }

    protected abstract Set<DependencyContext<?>> resolveSingle(ComponentContainer componentContainer, ApplicationContext applicationContext) throws DependencyResolutionException;

    protected Set<ComponentKey<?>> resolveDependencies(final ExecutableElementView<?> bindsMethod) {
        return bindsMethod.parameters().all().stream()
                .filter(parameter -> !parameter.annotations().has(HandledInjection.class))
                .map(this::resolveComponentKey)
                .collect(Collectors.toSet());
    }

    protected <T, E extends AnnotatedElementView & GenericTypeView<T>> ComponentKey<T> resolveComponentKey(final E element) {
        final TypeView<T> type = element.genericType();
        final Builder<T> keyBuilder = ComponentKey.builder(type.type());
        element.annotations().get(Named.class)
                .filter(qualifier -> StringUtilities.notEmpty(qualifier.value()))
                .peek(qualifier -> {
                    if (StringUtilities.notEmpty(qualifier.value())) {
                        keyBuilder.name(qualifier);
                    }
                });
        return keyBuilder.build();
    }
}
