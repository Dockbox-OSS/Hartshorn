/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.inject.CyclingConstructorAnalyzer;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentFinalizingPostProcessor implements ComponentPostProcessor {

    @Override
    public <T> T process(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        T finalizingInstance = instance;
        if (processingContext.containsKey(Key.of(ProxyFactory.class))) {
            final ProxyFactory<T, ?> factory = processingContext.get(Key.of(ProxyFactory.class));
            try {
                final boolean stateModified = factory instanceof StateAwareProxyFactory stateAwareProxyFactory && stateAwareProxyFactory.modified();
                final boolean noConcreteInstancePossible = instance == null && processingContext.type().isAbstract();
                if (stateModified || noConcreteInstancePossible) {
                    finalizingInstance = this.createProxyInstance(context, factory, instance);
                }
            }
            catch (final ApplicationException e) {
                throw new ApplicationRuntimeException(e);
            }
        }
        return context.get(ComponentPopulator.class).populate(finalizingInstance);
    }

    protected <T> T createProxyInstance(final ApplicationContext context, final ProxyFactory<T, ?> factory, @Nullable final T instance) throws ApplicationException {
        final TypeView<T> factoryType = context.environment().introspect(factory.type());
        // Ensure we use a non-default constructor if there is no default constructor to use
        if (!factoryType.isInterface() && factoryType.constructors().defaultConstructor().absent()) {
            final ConstructorView<T> constructor = CyclingConstructorAnalyzer.findConstructor(factoryType)
                    .rethrowUnchecked()
                    .orThrow(() -> new ApplicationException("No default or injectable constructor found for proxy factory " + factoryType.name()));

            final Object[] arguments = constructor.parameters().loadFromContext();
            return factory.proxy(constructor, arguments).or(instance);
        }
        return factory.proxy().or(instance);
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        return processingContext.get(Key.of(ComponentContainer.class)).permitsProxying();
    }

    @Override
    public Integer order() {
        // Run after all other core post processors, but permit external post processors to run after this one
        return Integer.MAX_VALUE / 2;
    }
}
