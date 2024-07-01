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

import org.dockbox.hartshorn.inject.ComponentKey;

/**
 * Resolves dependencies for a given binding declaration. This is used to determine the dependencies of a binding
 * declaration, which are then used to determine the order in which bindings are created. This is important for
 * resolving circular dependencies, and ensuring every component has its dependencies resolved before it is created.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface BindingDeclarationDependencyResolver {

    /**
     * Determines if this resolver can handle the given context. If this method returns {@code true}, the
     * {@link #dependencies(BindingStrategyContext)} method should be able to resolve the dependencies for the
     * given context.
     *
     * <p>This method should typically perform the minimum amount of work required to determine if the resolver
     * can handle the given context. It should not perform any actual dependency resolution, as that is the
     * responsibility of the {@link #dependencies(BindingStrategyContext)} method.
     *
     * @param context The context for which dependencies should be resolved
     * @return {@code true} when this resolver can handle the given context, {@code false} otherwise
     * @param <T> the type of the binding as declared by the context
     */
    <T> boolean canHandle(BindingStrategyContext<T> context);

    /**
     * Resolves the dependencies for the given context. The returned set of dependencies should be resolved before
     * the binding that is being resolved. This method should only be called when the {@link #canHandle(BindingStrategyContext)}
     * method returns {@code true}, and should not perform the same checks again.
     *
     * @param context The context for which dependencies should be resolved
     * @return The set of dependencies that should be resolved before the binding that is being resolved
     */
    Set<ComponentKey<?>> dependencies(BindingStrategyContext<?> context);
}
