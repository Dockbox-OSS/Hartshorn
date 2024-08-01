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

package org.dockbox.hartshorn.component.processing.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;

/**
 * TODO: #1060 Add documentation
 *
 * @param <P> ...
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public abstract class ProxyDelegationPostProcessor<P> extends ComponentPostProcessor {

    protected abstract Class<P> parentTarget();

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return processingContext.permitsProxying();
    }

    @Override
    public <T> void preConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        if (!processingContext.type().isChildOf(this.parentTarget())) {
            return;
        }

        ProxyFactory<P> factory = processingContext.get(ProxyFactory.class);
        if (factory == null) {
            return;
        }

        P concreteDelegator = this.concreteDelegator(context, factory, this.parentTarget());

        if (this.skipConcreteMethods()) {
            // Ensure we keep the original instance as delegate if possible, to avoid losing context. This rule is defined by the finalizing process.
            if (instance != null) {
                factory.advisors().type().delegate(this.parentTarget().cast(instance));
            }
            factory.advisors().type(this.parentTarget()).delegateAbstractOnly(concreteDelegator);
        }
        else {
            factory.advisors().type(this.parentTarget()).delegate(concreteDelegator);
        }
    }

    protected P concreteDelegator(ApplicationContext context, ProxyFactory<P> handler, Class<? extends P> parent) {
        return context.get(this.parentTarget());
    }

    protected boolean skipConcreteMethods() {
        return false;
    }
}
