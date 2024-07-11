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

package org.dockbox.hartshorn.inject.graph.resolve;

import java.util.Set;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.binding.HierarchyLookup;
import org.dockbox.hartshorn.inject.graph.AbstractContainerDependencyResolver;
import org.dockbox.hartshorn.inject.graph.ComponentContainerDependencyContext;
import org.dockbox.hartshorn.inject.graph.ComponentContainerDependencyDeclarationContext;
import org.dockbox.hartshorn.inject.graph.DependencyMap;
import org.dockbox.hartshorn.inject.graph.DependencyResolutionException;
import org.dockbox.hartshorn.inject.graph.ManagedComponentKeyDependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.ComponentKeyDependencyDeclarationContext;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.provider.ComponentConstructorResolver;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComponentDependencyResolver extends AbstractContainerDependencyResolver {

    private final InjectorEnvironment environment;
    private final IntrospectionDependencyResolver resolver;
    private final HierarchyLookup hierarchyLookup;

    protected ComponentDependencyResolver(InjectorEnvironment environment, HierarchyLookup hierarchyLookup) {
        this.environment = environment;
        this.resolver = new IntrospectionDependencyResolver(
                environment.injectionPointsResolver(),
                environment.componentKeyResolver()
        );
        this.hierarchyLookup = hierarchyLookup;
    }

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(
        DependencyDeclarationContext<T> declarationContext
    ) throws DependencyResolutionException {
        TypeView<T> type = declarationContext.type();
        ConstructorView<? extends T> constructorView;
        try {
            constructorView = ComponentConstructorResolver.create(this.environment, this.hierarchyLookup)
                    .findConstructor(type)
                    .orNull();
        }
        catch (Throwable throwable) {
            throw new DependencyResolutionException(throwable);
        }

        if (constructorView == null) {
            return Set.of();
        }
        Set<ComponentKey<?>> constructorDependencies = resolver.resolveDependencies(constructorView);
        Set<ComponentKey<?>> typeDependencies = resolver.resolveDependencies(type);

        DependencyMap dependencies = DependencyMap.create()
                .immediate(constructorDependencies)
                .delayed(typeDependencies);

        if (declarationContext instanceof ComponentContainerDependencyDeclarationContext<T> containerContext) {
            ComponentKey<T> componentKey = ComponentKey.of(type);
            return Set.of(new ComponentContainerDependencyContext<>(containerContext.container(), componentKey, dependencies, constructorView));
        }
        else if (declarationContext instanceof ComponentKeyDependencyDeclarationContext<T> keyContext) {
            Provider<T> provider = keyContext.provider();
            ManagedComponentKeyDependencyContext<T> dependencyContext = ManagedComponentKeyDependencyContext.builder(keyContext.key(), type)
                .dependencies(dependencies)
                .constructorView(constructorView)
                .lazy(provider.defaultLazy().booleanValue())
                .lifecycleType(provider.defaultLifecycle())
                .build();
            return Set.of(dependencyContext);
        }
        else {
            return Set.of();
        }
    }
}
