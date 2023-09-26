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

import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.strategy.DependencyResolverUtils;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentDependencyResolver extends AbstractContainerDependencyResolver {

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(final DependencyDeclarationContext<T> componentContainer, final ApplicationContext applicationContext) throws DependencyResolutionException {
        final TypeView<?> type = componentContainer.type();
        final ConstructorView<?> constructorView = ComponentConstructorResolver.create(applicationContext).findConstructor(type)
                .mapError(DependencyResolutionException::new)
                .rethrow()
                .orNull();

        if (constructorView == null) {
            return Set.of();
        }

        final Set<ComponentKey<?>> constructorDependencies = DependencyResolverUtils.resolveDependencies(constructorView);
        final Set<ComponentKey<?>> typeDependencies = DependencyResolverUtils.resolveDependencies(type);

        final ComponentKey<?> componentKey = ComponentKey.of(type.type());
        final Set<ComponentKey<?>> dependencies = CollectionUtilities.merge(constructorDependencies, typeDependencies);

        return Set.of(new ManagedComponentDependencyContext<>(componentKey, dependencies));
    }
}
