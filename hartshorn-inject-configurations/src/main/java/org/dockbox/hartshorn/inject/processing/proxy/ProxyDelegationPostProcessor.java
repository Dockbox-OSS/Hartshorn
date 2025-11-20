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
 * An abstract {@link ComponentPostProcessor} that is used to delegate non-implemented methods of a
 * parent type to a concrete implementation of that parent type. By default, the concrete
 * implementation is provided by the {@link InjectionCapableApplication}, though this behavior may
 * be changed by overriding
 * {@link #concreteDelegator(InjectionCapableApplication, ProxyFactory, Class) the concrete
 * delegator} method.
 *
 * @param <P> the type of the parent that is being delegated to
 *
 * @author Guus Lieben
 * @since 0.4.8
 */
public abstract class ProxyDelegationPostProcessor<P> extends ComponentPostProcessor {

    /**
     * The parent target class of which a component must be a child in order to be eligible for
     * proxy delegation.
     *
     * @return the parent target class
     */
    protected abstract Class<P> parentTarget();

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return processingContext.permitsProxying() && processingContext.type()
            .isChildOf(this.parentTarget());
    }

    @Override
    public <T> void preConfigureComponent(
        InjectionCapableApplication application,
        @Nullable T instance,
        ComponentProcessingContext<T> processingContext
    ) {
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

    /**
     * Provides the concrete delegator instance to which methods will be delegated. By default, this
     * method retrieves an instance from the application's default provider.
     *
     * @param application the injection-capable application
     * @param handler the proxy factory handling the proxy creation
     * @param parent the parent class being delegated to
     *
     * @return the concrete delegator instance
     */
    protected P concreteDelegator(
        InjectionCapableApplication application,
        ProxyFactory<P> handler,
        Class<? extends P> parent
    ) {
        return application.defaultProvider().get(this.parentTarget());
    }

    /**
     * Whether to skip concrete methods when delegating to the concrete implementation. If true,
     * only abstract methods will be delegated.
     *
     * @return true to skip concrete methods, false to delegate all methods
     */
    protected boolean skipConcreteMethods() {
        return false;
    }
}
