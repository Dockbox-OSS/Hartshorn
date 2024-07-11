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

package org.dockbox.hartshorn.launchpad.graph;

import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.graph.AbstractConfigurationDependencyVisitor;
import org.dockbox.hartshorn.inject.graph.ConfigurationDependencyVisitor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessorRegistry;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.graph.ComponentConfigurationException;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.binding.BindingFunction;

/**
 * {@link ConfigurationDependencyVisitor} implementation that delegates the configuration of the dependency to the provided
 * {@link DependencyContext}, through {@link DependencyContext#configure(BindingFunction)}.
 *
 * <p>Additional hooks are in place to register {@link ComponentProcessor}s to the given {@link ComponentProcessorRegistry}
 * immediately after they have been configured.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class DelegatingConfigurationDependencyVisitor extends AbstractConfigurationDependencyVisitor {

    private final ComponentProcessorRegistry processorRegistry;

    public DelegatingConfigurationDependencyVisitor(
            Binder binder,
            ComponentProvider componentProvider,
            ComponentProcessorRegistry processorRegistry
    ) {
        super(binder, componentProvider);
        this.processorRegistry = processorRegistry;
    }

    @Override
    public <T> void registerProvider(DependencyContext<T> dependencyContext) throws ComponentConfigurationException {
        BindingFunction<T> function = this.binder().bind(dependencyContext.componentKey());
        dependencyContext.configure(function);
    }

    @Override
    public void doAfterRegister(DependencyContext<?> dependencyContext) {
        if(ComponentProcessor.class.isAssignableFrom(dependencyContext.componentKey().type())) {
            ComponentProcessor processor = (ComponentProcessor) this.componentProvider().get(dependencyContext.componentKey());
            this.processorRegistry.register(processor);
        }
    }
}
