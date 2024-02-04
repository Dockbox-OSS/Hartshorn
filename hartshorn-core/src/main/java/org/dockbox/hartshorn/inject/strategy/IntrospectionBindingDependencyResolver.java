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

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;

public class IntrospectionBindingDependencyResolver implements BindingDeclarationDependencyResolver {

    private final IntrospectionDependencyResolver introspectionDependencyResolver;

    public IntrospectionBindingDependencyResolver(ApplicationEnvironment environment) {
        this.introspectionDependencyResolver = new IntrospectionDependencyResolver(environment);
    }

    @Override
    public <T> boolean canHandle(BindingStrategyContext<T> context) {
        return context instanceof MethodAwareBindingStrategyContext<T>;
    }

    @Override
    public Set<ComponentKey<?>> dependencies(BindingStrategyContext<?> context) {
        MethodAwareBindingStrategyContext<?> strategyContext = (MethodAwareBindingStrategyContext<?>) context;
        return this.introspectionDependencyResolver.resolveDependencies(strategyContext.method());
    }
}
