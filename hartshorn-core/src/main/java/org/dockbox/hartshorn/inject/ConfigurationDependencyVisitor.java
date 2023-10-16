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
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

public class ConfigurationDependencyVisitor extends BreadthFirstGraphVisitor<DependencyContext<?>> {

    private final ApplicationContext applicationContext;

    public ConfigurationDependencyVisitor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected boolean visit(GraphNode<DependencyContext<?>> node) throws GraphException {
        DependencyContext<?> dependencyContext = node.value();
        try {
            this.registerProvider(dependencyContext);
            this.doAfterRegister(dependencyContext);
            return true;
        }
        catch (ComponentConfigurationException e) {
            throw new GraphException(e);
        }
    }

    private <T> void registerProvider(DependencyContext<T> dependencyContext) throws ComponentConfigurationException {
        BindingFunction<T> function = this.applicationContext.bind(dependencyContext.componentKey());
        dependencyContext.configure(function);
    }

    private void doAfterRegister(DependencyContext<?> dependencyContext) {
        if (ComponentProcessor.class.isAssignableFrom(dependencyContext.componentKey().type())) {
            ComponentProcessor processor = (ComponentProcessor) this.applicationContext.get(dependencyContext.componentKey());
            this.applicationContext.add(processor);
        }
    }
}
