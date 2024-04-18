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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * A simple implementation of {@link DependencyResolver} that delegates bulk resolution to a single
 * {@link #resolveSingle(DependencyDeclarationContext, ApplicationContext) resolution method}. This method is called for each
 * {@link DependencyDeclarationContext} that is passed to {@link #resolve(Collection)}.
 *
 * @see DependencyResolver
 * @see DependencyDeclarationContext
 * @see DependencyContext
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractContainerDependencyResolver implements DependencyResolver {

    private final ApplicationContext applicationContext;

    protected AbstractContainerDependencyResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public Set<DependencyContext<?>> resolve(Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException {
        Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        for (DependencyDeclarationContext<?> componentContainer : containers) {
            dependencyContexts.addAll(this.resolveSingle(componentContainer, this.applicationContext));
        }
        return dependencyContexts;
    }

    /**
     * Resolves a single {@link DependencyDeclarationContext} into a collection of {@link DependencyContext} instances. The result
     * of this method may contain zero or more {@link DependencyContext} instances, each representing a dependency that was declared
     * by the {@link DependencyDeclarationContext} that was passed to this method.
     *
     * @param declarationContext the declaration to resolve
     * @param applicationContext the application context
     *
     * @return a collection of {@link DependencyContext} instances
     *
     * @param <T> the type of the component that is declared by the declaration
     * @throws DependencyResolutionException when the resolution fails
     */
    protected abstract <T> Set<DependencyContext<?>> resolveSingle(DependencyDeclarationContext<T> declarationContext, ApplicationContext applicationContext) throws DependencyResolutionException;

}
