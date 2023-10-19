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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractContainerDependencyResolver implements DependencyResolver {

    @Override
    public Set<DependencyContext<?>> resolve(Collection<DependencyDeclarationContext<?>> containers, ApplicationContext applicationContext) throws DependencyResolutionException {
        Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        for (DependencyDeclarationContext<?> componentContainer : containers) {
            dependencyContexts.addAll(this.resolveSingle(componentContainer, applicationContext));
        }
        return dependencyContexts;
    }

    protected abstract <T> Set<DependencyContext<?>> resolveSingle(DependencyDeclarationContext<T> componentContainer, ApplicationContext applicationContext) throws DependencyResolutionException;

}
