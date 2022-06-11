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

package org.dockbox.hartshorn.proxy.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.FunctionalComponentPostProcessor;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyFactory;

import java.lang.annotation.Annotation;

public abstract class ProxyDelegationPostProcessor<P, A extends Annotation> extends FunctionalComponentPostProcessor {

    protected abstract Class<P> parentTarget();

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        return key.type().childOf(this.parentTarget());
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        throw new UnsupportedOperationException("Processing service methods without a context is not supported");
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        final ProxyFactory factory = processingContext.get(Key.of(ProxyFactory.class));
        if (factory == null) return instance;

        if (this.skipConcreteMethods()) {
            // Ensure we keep the original instance as delegate, to avoid losing context. This rule is defined by the finalizing process.
            factory.delegate(instance);
            factory.delegateAbstract(this.parentTarget(), this.concreteDelegator(context, factory, TypeContext.of(this.parentTarget())));
        }
        else {
            factory.delegate(this.parentTarget(), this.concreteDelegator(context, factory, TypeContext.of(this.parentTarget())));
        }

        return instance;
    }

    protected P concreteDelegator(final ApplicationContext context, final ProxyFactory<P, ?> handler, final TypeContext<? extends P> parent) {
        return context.get(this.parentTarget());
    }

    protected boolean skipConcreteMethods() {
        return false;
    }
}
