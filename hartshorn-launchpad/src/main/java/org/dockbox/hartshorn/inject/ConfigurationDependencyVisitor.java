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

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;
import org.dockbox.hartshorn.inject.graph.ComponentConfigurationException;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.DependencyGraph;
import org.dockbox.hartshorn.inject.processing.ComponentProcessor;
import org.dockbox.hartshorn.util.graph.GraphIterator;

/**
 * A visitor that visits all {@link DependencyContext}s in a {@link DependencyGraph}, allowing them to be
 * configured or registered in the owning context.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ConfigurationDependencyVisitor extends GraphIterator<DependencyContext<?>>, ApplicationContextCarrier {

    /**
     * Registers the given {@link DependencyContext} with the {@link ApplicationContext}.
     *
     * @param dependencyContext the context to register
     * @param <T> the type of the component that is registered
     * @throws ComponentConfigurationException when the context cannot be registered
     */
    <T> void registerProvider(DependencyContext<T> dependencyContext) throws ComponentConfigurationException;

    /**
     * Invoked after a {@link DependencyContext} has been registered with the {@link ApplicationContext}.
     * This may be used to perform additional actions, such as registering a {@link ComponentProcessor}
     * with the {@link ApplicationContext}.
     *
     * @param dependencyContext the context that was registered
     */
    void doAfterRegister(DependencyContext<?> dependencyContext);
}
