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

package org.dockbox.hartshorn.inject.graph.declaration;

import java.util.Set;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.graph.ComponentConfigurationException;
import org.dockbox.hartshorn.inject.graph.ComponentMemberType;
import org.dockbox.hartshorn.inject.graph.DependencyMap;
import org.dockbox.hartshorn.inject.graph.DependencyResolutionType;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.introspect.view.View;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A dependency context represents the definition of a single dependency, along with its
 * dependencies, scope and priority. The context can be used to automatically configure
 * a {@link BindingFunction} for the dependency.
 *
 * @param <T> the type of the dependency
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DependencyContext<T> {

    /**
     * Returns the key that is used to identify the dependency in dependency visitors or
     * graphs.
     *
     * @return the key of the dependency
     */
    ComponentKey<T> componentKey();

    /**
     * Returns all dependencies of the dependency. This includes dependencies of all
     * {@link DependencyResolutionType resolution types}.
     *
     * @return all dependencies of the dependency
     */
    DependencyMap dependencies();

    /**
     * Returns all dependencies of the dependency, of the given {@link DependencyResolutionType}.
     * If the dependency does not have any dependencies of the given type, an empty set is
     * returned.
     *
     * @param resolutionType the type of dependencies to return
     * @return all dependencies of the dependency, of the given type
     */
    Set<ComponentKey<?>> dependencies(DependencyResolutionType resolutionType);

    /**
     * Returns whether the dependency needs to be resolved immediately. If the dependency
     * is not part of this context, {@code false} is returned.
     *
     * @param dependencyCandidate the dependency to check
     * @return whether the dependency needs to be resolved immediately
     *
     * @see DependencyResolutionType#IMMEDIATE
     */
    boolean needsImmediateResolution(ComponentKey<?> dependencyCandidate);

    /**
     * Returns the priority of the dependency. This priority is used to determine the order
     * in which dependencies are instantiated.
     *
     * @return the priority of the dependency
     */
    int priority();

    /**
     * Returns the scope of the dependency. The scope determines the lifecycle of the
     * dependency.
     *
     * @return the scope of the dependency
     *
     * @see Scope
     */
    Option<ScopeKey> scope();

    /**
     * Returns the type of the binding. The type determines how the dependency is bound to
     * the container.
     *
     * @return the type of the binding
     */
    ComponentMemberType memberType();

    /**
     * Configures the given binding function for the dependency. The binding function is
     * used to configure the dependency in the container. The implementation may decide
     * whether to configure the dependency as whichever binding it prefers, or default to
     * a no-op.
     *
     * @param function the binding function to configure
     * @throws ComponentConfigurationException when the binding could not be configured
     */
    void configure(BindingFunction<T> function) throws ComponentConfigurationException;

    /**
     * Returns the origin of the dependency. The origin is the location where the dependency
     * is defined. This is typically a constructor, field or method.
     *
     * @return the origin of the dependency
     */
    View origin();

    boolean lazy();

    LifecycleType lifecycleType();

    boolean processAfterInitialization();
}
