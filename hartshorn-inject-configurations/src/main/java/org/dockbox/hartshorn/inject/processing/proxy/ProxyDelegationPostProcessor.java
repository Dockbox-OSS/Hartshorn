/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;

/**
 * An abstract {@link ComponentPostProcessor} that is used to delegate non-implemented methods of a parent
 * type to a concrete implementation of that parent type. By default, the concrete implementation is provided
 * by the {@link InjectionCapableApplication}, though this behavior may be changed by overriding {@link
 * #concreteDelegator(InjectionCapableApplication, ProxyFactory, Class) the concrete delegator} method.
 *
 * @param <P> the type of the parent that is being delegated to
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public abstract class ProxyDelegationPostProcessor<P> extends ComponentPostProcessor {

    protected abstract Class<P> parentTarget();

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return processingContext.permitsProxying() && processingContext.type().isChildOf(this.parentTarget());
    }

    @Override
    public <T> void preConfigureComponent(InjectionCapableApplication application, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        ProxyFactory<P> factory = processingContext.get(ProxyFactory.class);
        if (factory == null) {
            return;
        }

        P concreteDelegator = this.concreteDelegator(application, factory, this.parentTarget());

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

    protected P concreteDelegator(InjectionCapableApplication application, ProxyFactory<P> handler, Class<? extends P> parent) {
        return application.defaultProvider().get(this.parentTarget());
    }

    protected boolean skipConcreteMethods() {
        return false;
    }
}
