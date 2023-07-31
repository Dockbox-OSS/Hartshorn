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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.inject.CyclingConstructorAnalyzer;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentFinalizingPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> T initializeComponent(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {

        final boolean permitsProxying = !processingContext.containsKey(ComponentKey.of(ComponentContainer.class))
                || processingContext.get(ComponentKey.of(ComponentContainer.class)).permitsProxying();

        if (permitsProxying) {
            T finalizingInstance = instance;
            if (processingContext.containsKey(ComponentKey.of(ProxyFactory.class))) {
                final ProxyFactory<T> factory = processingContext.get(ComponentKey.of(ProxyFactory.class));
                try {
                    final boolean stateModified = factory instanceof StateAwareProxyFactory<T> stateAwareProxyFactory && stateAwareProxyFactory.modified();
                    final boolean noConcreteInstancePossible = instance == null && processingContext.type().modifiers().isAbstract();
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
        return instance;
    }

    protected <T> T createProxyInstance(final ApplicationContext context, final ProxyFactory<T> factory, @Nullable final T instance) throws ApplicationException {
        final TypeView<T> factoryType = context.environment().introspect(factory.type());
        // Ensure we use a non-default constructor if there is no default constructor to use
        if (!factoryType.isInterface() && factoryType.constructors().defaultConstructor().absent()) {
            final ConstructorView<T> constructor = CyclingConstructorAnalyzer.findConstructor(factoryType)
                    .rethrow()
                    .orElseThrow(() -> new ApplicationException("No default or injectable constructor found for proxy factory " + factoryType.name()));

            final ViewContextAdapter adapter = context.get(ViewContextAdapter.class);
            final Object[] arguments = adapter.loadParameters(constructor);
            return factory.proxy(constructor, arguments).orElse(instance);
        }
        return factory.proxy().orElse(instance);
    }

    @Override
    public int priority() {
        // Run after all other core post processors, but permit external post processors to run after this one
        return ProcessingPriority.LOWEST_PRECEDENCE - 128;
    }
}
