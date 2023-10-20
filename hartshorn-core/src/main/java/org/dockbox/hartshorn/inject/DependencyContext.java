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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * A dependency context represents the definition of a single dependency, along with its
 * dependencies, scope and priority. The context can be used to automatically configure
 * a {@link BindingFunction} for the dependency.
 *
 * @param <T> the type of the dependency
 *
 * @author Guus Lieben
 * @since 0.5.0
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
     * Returns the dependencies of the dependency. These dependencies are required to be
     * available before the dependency can be instantiated.
     *
     * @return the dependencies of the dependency
     */
    Set<ComponentKey<?>> dependencies();

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
    Class<? extends Scope> scope();

    /**
     * Returns whether the dependency is a singleton. The implementation may decide whether
     * to configure the dependency as whichever binding it prefers, or default to a no-op.
     *
     * @param function the binding function to configure
     * @throws ComponentConfigurationException when the binding could not be configured
     */
    void configure(BindingFunction<T> function) throws ComponentConfigurationException;

    View origin();
}
