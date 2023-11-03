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
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentDependencyResolver extends AbstractContainerDependencyResolver {

    protected ComponentDependencyResolver(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(DependencyDeclarationContext<T> componentContainer, ApplicationContext applicationContext) throws DependencyResolutionException {
        TypeView<T> type = componentContainer.type();
        ConstructorView<? extends T> constructorView = ComponentConstructorResolver.create(applicationContext).findConstructor(type)
                .mapError(DependencyResolutionException::new)
                .rethrow()
                .orNull();

        if (constructorView == null) {
            return Set.of();
        }

        Set<ComponentKey<?>> constructorDependencies = DependencyResolverUtils.resolveDependencies(constructorView);
        Set<ComponentKey<?>> typeDependencies = DependencyResolverUtils.resolveDependencies(type);

        DependencyMap dependencies = DependencyMap.create()
                .immediate(constructorDependencies)
                .delayed(typeDependencies);

        ComponentKey<T> componentKey = ComponentKey.of(type);
        return Set.of(new ManagedComponentDependencyContext<>(componentKey, dependencies, constructorView));
    }
}
