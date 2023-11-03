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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.AbstractDependencyContext;
import org.dockbox.hartshorn.inject.ComponentConfigurationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.introspect.view.View;

public class ImplementationDependencyContext<T, I extends T> extends AbstractDependencyContext<I> {

    private final DependencyContext<I> implementationContext;
    private final DependencyContext<T> declarationContext;

    public ImplementationDependencyContext(DependencyContext<I> implementationContext, DependencyContext<T> declarationContext) {
        super(implementationContext.componentKey(), implementationContext.dependencies(), implementationContext.scope(), implementationContext.priority());
        this.implementationContext = implementationContext;
        this.declarationContext = declarationContext;
    }

    public DependencyContext<I> implementationContext() {
        return implementationContext;
    }

    public DependencyContext<T> declarationContext() {
        return declarationContext;
    }

    @Override
    public void configure(BindingFunction<I> function) throws ComponentConfigurationException {
        this.implementationContext.configure(function);
    }

    @Override
    public View origin() {
        return implementationContext.origin();
    }
}
