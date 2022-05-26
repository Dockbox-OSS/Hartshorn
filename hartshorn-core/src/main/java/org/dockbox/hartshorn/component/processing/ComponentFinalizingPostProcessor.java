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
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;

public class ComponentFinalizingPostProcessor implements ComponentPostProcessor {

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        T finalizingInstance = instance;
        if (processingContext.containsKey(Key.of(ProxyFactory.class))) {
            final ProxyFactory<T, ?> factory = processingContext.get(Key.of(ProxyFactory.class));
            try {
                if (((StateAwareProxyFactory<?, ?>) factory).modified() || (instance == null && key.type().isAbstract())) {
                    finalizingInstance = factory.proxy().or(instance);
                }
            }
            catch (final ApplicationException e) {
                ExceptionHandler.unchecked(e);
            }
        }
        return context.get(ComponentPopulator.class).populate(finalizingInstance);
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        throw new UnsupportedOperationException("Finalizing without a context is not supported");
    }

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        return processingContext.get(Key.of(ComponentContainer.class)).permitsProxying();
    }

    @Override
    public Integer order() {
        // Run after all other core post processors, but permit external post processors to run after this one
        return Integer.MAX_VALUE / 2;
    }
}
