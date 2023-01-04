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
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.util.TypeUtils;

public abstract class ContextConfiguringComponentProcessor<C extends Context> extends ComponentPostProcessor {

    private final Class<C> contextType;

    protected ContextConfiguringComponentProcessor(final Class<C> contextType) {
        this.contextType = contextType;
    }

    @Override
    public <T> T process(final ApplicationContext context, @Nullable final T instance,
                         final ComponentProcessingContext<T> processingContext) {

        if (this.supports(processingContext)) {
            final C componentContext = processingContext.first(ContextKey.of(this.contextType))
                    .orCompute(() -> this.createContext(context, processingContext)).orNull();

            if (componentContext != null) {
                this.configure(context, componentContext, processingContext);
            }

            if (instance instanceof Context contextInstance) {
                contextInstance.add(componentContext);
            }
            else {
                final ComponentKey<ProxyFactory<T, ?>> factoryKey = TypeUtils.adjustWildcards(ComponentKey.of(ProxyFactory.class), ComponentKey.class);
                if (processingContext.containsKey(factoryKey)) {
                    final ProxyFactory<T, ?> proxyFactory = processingContext.get(factoryKey);
                    proxyFactory.contextContainer().add(componentContext);
                }
            }
        }
        return instance;
    }

    protected abstract boolean supports(final ComponentProcessingContext<?> processingContext);

    protected abstract <T> void configure(final ApplicationContext context, final C componentContext,
                                          final ComponentProcessingContext<T> processingContext);

    protected abstract C createContext(final ApplicationContext context,
                                       final ComponentProcessingContext<?> processingContext);
}
