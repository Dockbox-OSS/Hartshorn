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

package org.dockbox.hartshorn.inject.strategy;

import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.component.ComponentKey;

/**
 * A composite dependency resolver that combines multiple dependency resolvers into a single resolver.
 * This resolver will delegate to all resolvers that can handle the given context and combine the results
 * into a single set of dependencies.
 *
 * @param resolvers The resolvers to combine
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public record CompositeBindingDependencyResolver(Set<BindingDeclarationDependencyResolver> resolvers) implements BindingDeclarationDependencyResolver {

    @Override
    public <T> boolean canHandle(BindingStrategyContext<T> context) {
        return this.resolvers.stream().anyMatch(resolver -> resolver.canHandle(context));
    }

    @Override
    public Set<ComponentKey<?>> dependencies(BindingStrategyContext<?> context) {
        return this.resolvers.stream()
                .filter(resolver -> resolver.canHandle(context))
                .flatMap(resolver -> resolver.dependencies(context).stream())
                .collect(Collectors.toSet());
    }
}
