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

package org.dockbox.hartshorn.component.processing.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.FunctionalComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.Collection;

public abstract class ServiceMethodInterceptorPostProcessor extends FunctionalComponentPostProcessor {

    @Override
    public <T> void preConfigureComponent(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext) {
        final Collection<MethodView<T, ?>> methods = this.modifiableMethods(processingContext);

        final ProxyFactory<T> factory = processingContext.get(ComponentKey.of(ProxyFactory.class));
        if (factory == null) {
            return;
        }

        for (final MethodView<T, ?> method : methods) {
            final MethodProxyContext<T> ctx = new MethodProxyContextImpl<>(context, processingContext.type(), method);

            if (this.preconditions(context, ctx, processingContext)) {
                final MethodInterceptor<T, ?> function = this.process(context, ctx, processingContext);
                if (function != null) factory.advisors().method(method).intercept(TypeUtils.adjustWildcards(function, MethodInterceptor.class));
            }
            else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }
    }

    protected abstract <T> Collection<MethodView<T, ?>> modifiableMethods(ComponentProcessingContext<T> processingContext);

    public abstract <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext);

    public abstract <T, R> MethodInterceptor<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext);

    public boolean failOnPrecondition() {
        return true;
    }

    @Override
    public int priority() {
        return ProcessingPriority.HIGH_PRECEDENCE;
    }
}
