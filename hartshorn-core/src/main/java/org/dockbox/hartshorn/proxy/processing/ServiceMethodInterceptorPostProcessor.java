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
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.ProxyFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ServiceMethodInterceptorPostProcessor<A extends Annotation> extends FunctionalComponentPostProcessor<A> {

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        throw new UnsupportedOperationException("Processing service methods without a context is not supported");
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        final TypeContext<T> type = key.type();
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(type);

        final ProxyFactory factory = processingContext.get(Key.of(ProxyFactory.class));
        if (factory == null) return instance;

        for (final MethodContext<?, T> method : methods) {
            final MethodProxyContext ctx = new MethodProxyContextImpl<>(context, type, method);

            if (this.preconditions(context, ctx, processingContext)) {
                final MethodInterceptor<Object> function = this.process(context, ctx, processingContext);
                if (function != null) factory.intercept(method, function);
            }
            else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }

        return instance;
    }

    protected abstract <T> Collection<MethodContext<?, T>> modifiableMethods(TypeContext<T> type);

    public abstract <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext processingContext);

    public abstract <T, R> MethodInterceptor<T> process(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext processingContext);

    public boolean failOnPrecondition() {
        return true;
    }

    @Override
    public Integer order() {
        return ProcessingOrder.EARLY;
    }
}
