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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.ContextKey;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.util.TypeUtils;

/**
 * TODO: #1060 Add documentation
 *
 * @param <C> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class ContextConfiguringComponentProcessor<C extends ContextView> extends ComponentPostProcessor {

    private final Class<C> contextType;

    protected ContextConfiguringComponentProcessor(Class<C> contextType) {
        this.contextType = contextType;
    }

    @Override
    public <T> void preConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        if (this.supports(processingContext)) {
            C componentContext = processingContext.firstContext(ContextKey.of(this.contextType))
                    .orCompute(() -> this.createContext(context, processingContext)).orNull();

            if (componentContext != null) {
                this.configure(context, componentContext, processingContext);
            }

            if (instance instanceof Context contextInstance) {
                contextInstance.addContext(componentContext);
            }
            else {
                ComponentKey<ProxyFactory<T>> factoryKey = TypeUtils.adjustWildcards(ComponentKey.of(ProxyFactory.class), ComponentKey.class);
                if (processingContext.containsKey(factoryKey)) {
                    ProxyFactory<T> proxyFactory = processingContext.get(factoryKey);
                    proxyFactory.contextContainer().addContext(componentContext);
                }
            }
        }
    }

    protected abstract boolean supports(
            ComponentProcessingContext<?> processingContext
    );

    protected abstract <T> void configure(
            ApplicationContext context,
            C componentContext,
            ComponentProcessingContext<T> processingContext
    );

    protected abstract C createContext(
            ApplicationContext context,
            ComponentProcessingContext<?> processingContext
    );
}
