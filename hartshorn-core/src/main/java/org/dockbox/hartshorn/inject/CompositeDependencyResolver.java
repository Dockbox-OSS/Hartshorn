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
 * A composite dependency resolver is a {@link DependencyResolver} that delegates to a collection of other dependency
 * resolvers. The result of the delegation is a collection of all resolved dependencies, as resolved by the delegates.
 *
 * @see DependencyResolver
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CompositeDependencyResolver implements DependencyResolver {

    private final Set<DependencyResolver> resolvers;
    private final ApplicationContext applicationContext;

    public CompositeDependencyResolver(Set<DependencyResolver> resolvers, ApplicationContext applicationContext) {
        this.resolvers = resolvers;
        this.applicationContext = applicationContext;
    }

    /**
     * Returns a copy of the collection of resolvers that is used by this instance.
     *
     * @return A copy of the collection of resolvers that is used by this instance.
     */
    public Set<DependencyResolver> resolvers() {
        return Set.copyOf(this.resolvers);
    }

    @Override
    public Set<DependencyContext<?>> resolve(Collection<DependencyDeclarationContext<?>> declarationContexts) throws DependencyResolutionException {
        Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        for (DependencyResolver resolver : this.resolvers()) {
            Set<DependencyContext<?>> resolvedDependencies = resolver.resolve(declarationContexts);
            dependencyContexts.addAll(resolvedDependencies);
        }
        return dependencyContexts;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
