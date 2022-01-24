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

package org.dockbox.hartshorn.core.services;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.MethodInterceptor;
import org.dockbox.hartshorn.core.proxy.ProxyFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ProxyDelegationPostProcessor<P, A extends Annotation> extends ServiceMethodInterceptorPostProcessor<A> {

    protected abstract Class<P> parentTarget();

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return key.type().childOf(this.parentTarget());
    }

    @Override
    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final TypeContext<T> type) {
        return type.methods().stream().filter(method -> method.parent().is(this.parentTarget())).toList();
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return methodContext.method().parent().is(this.parentTarget());
    }

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final TypeContext<P> parentContext = TypeContext.of(this.parentTarget());
        final ProxyFactory factory = processingContext.get(Key.of(ProxyFactory.class));
        factory.delegate(methodContext.method().method(), this.concreteDelegator(context, factory, parentContext));
        return null;
    }

    protected P concreteDelegator(final ApplicationContext context, final ProxyFactory<P, ?> handler, final TypeContext<? extends P> parent) {
        return context.get(this.parentTarget());
    }
}
