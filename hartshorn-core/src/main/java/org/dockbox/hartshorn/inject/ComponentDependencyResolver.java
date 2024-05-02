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

package org.dockbox.hartshorn.inject;

import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.strategy.IntrospectionDependencyResolver;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentDependencyResolver extends AbstractContainerDependencyResolver {

    protected ComponentDependencyResolver(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected <T> Set<DependencyContext<?>> resolveSingle(
        DependencyDeclarationContext<T> declarationContext,
        ApplicationContext applicationContext
    ) throws DependencyResolutionException {
        TypeView<T> type = declarationContext.type();
        ConstructorView<? extends T> constructorView;
        try {
            constructorView = ComponentConstructorResolver.create(applicationContext)
                    .findConstructor(type)
                    .orNull();
        }
        catch (Throwable throwable) {
            throw new DependencyResolutionException(throwable);
        }

        if (constructorView == null) {
            return Set.of();
        }

        IntrospectionDependencyResolver resolver = new IntrospectionDependencyResolver(this.applicationContext().environment());
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
