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

import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

public class ConfigurationDependencyVisitor extends BreadthFirstGraphVisitor<DependencyContext<?>> {

    private final Binder binder;

    public ConfigurationDependencyVisitor(final Binder binder) {
        this.binder = binder;
    }

    @Override
    protected boolean visit(final GraphNode<DependencyContext<?>> node) throws GraphException {
        final DependencyContext<?> dependencyContext = node.value();
        try {
            this.registerProvider(dependencyContext);
            return true;
        }
        catch (final ComponentConfigurationException e) {
            throw new GraphException(e);
        }
    }

    private <T> void registerProvider(final DependencyContext<T> dependencyContext) throws ComponentConfigurationException {
        final BindingFunction<T> function = this.binder.bind(dependencyContext.componentKey());
        dependencyContext.configure(function);
    }
}
