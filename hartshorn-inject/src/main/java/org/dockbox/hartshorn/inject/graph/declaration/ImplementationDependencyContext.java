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

import org.dockbox.hartshorn.inject.graph.ComponentConfigurationException;
import org.dockbox.hartshorn.inject.provider.TypeAwareInstantiationStrategy;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * A {@link DependencyContext} implementation that is used for implementation components. Implementation components are
 * components that have a known implementation specification for which a {@link DependencyContext} is available. Typically,
 * these are obtained through {@link TypeAwareInstantiationStrategy}s that are registered with the {@link BindingHierarchy} of the
 * container.
 *
 * @param <T> the type of the component that is implemented
 * @param <I> the type of the component that is the implementation
 *
 * @see DependencyContext
 * @see TypeAwareInstantiationStrategy
 * @see BindingHierarchy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ImplementationDependencyContext<T, I extends T> extends AbstractDependencyContext<I> {

    private final DependencyContext<I> implementationContext;
    private final DependencyContext<T> declarationContext;

    public ImplementationDependencyContext(DependencyContext<I> implementationContext, DependencyContext<T> declarationContext) {
        super(implementationContext);
        this.implementationContext = implementationContext;
        this.declarationContext = declarationContext;
    }

    /**
     * Returns the {@link DependencyContext} of the implementation component. This is the component that is created by
     * the container.
     *
     * @return the {@link DependencyContext} of the implementation component
     */
    public DependencyContext<I> implementationContext() {
        return this.implementationContext;
    }

    /**
     * Returns the {@link DependencyContext} of the declaration component. This is the component that is declared by the
     * user, and is being implemented by the {@link #implementationContext()}. This component is not created by the
     * container.
     *
     * @return the {@link DependencyContext} of the declaration component
     */
    public DependencyContext<T> declarationContext() {
        return this.declarationContext;
    }

    @Override
    public void configure(BindingFunction<I> function) throws ComponentConfigurationException {
        // Do nothing, based on already existing configuration
    }

    @Override
    public View origin() {
        return this.implementationContext.origin();
    }
}
