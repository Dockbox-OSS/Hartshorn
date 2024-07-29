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

package org.dockbox.hartshorn.inject.processing.proxy;

import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public abstract class ServiceMethodInterceptorPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return processingContext.permitsProxying();
    }

    @Override
    public <T> void preConfigureComponent(InjectionCapableApplication application, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        Collection<MethodView<T, ?>> methods = this.modifiableMethods(processingContext);

        ProxyFactory<T> factory = processingContext.get(ProxyFactory.class);
        if (factory == null) {
            return;
        }

        for (MethodView<T, ?> method : methods) {
            MethodProxyContext<T> context = new MethodProxyContextImpl<>(application, processingContext.type(), method);

            if (this.preconditions(application, context, processingContext)) {
                MethodInterceptor<T, ?> function = this.process(application, context, processingContext);
                if (function != null) {
                    factory.advisors().method(method).intercept(TypeUtils.unchecked(function, MethodInterceptor.class));
                }
            }
            else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }
    }

    protected abstract <T> Collection<MethodView<T, ?>> modifiableMethods(ComponentProcessingContext<T> processingContext);

    public abstract <T> boolean preconditions(InjectionCapableApplication application, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext);

    public abstract <T, R> MethodInterceptor<T, R> process(InjectionCapableApplication application, MethodProxyContext<T> methodContext, ComponentProcessingContext<T> processingContext);

    public boolean failOnPrecondition() {
        return true;
    }
}
