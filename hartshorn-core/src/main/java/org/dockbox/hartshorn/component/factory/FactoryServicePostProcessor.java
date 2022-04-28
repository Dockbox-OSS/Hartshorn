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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.proxy.MethodInterceptor;

@AutomaticActivation
public class FactoryServicePostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<Factory, Service> {

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public Class<Factory> annotation() {
        return Factory.class;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final MethodContext<?, T> method = methodContext.method();
        final boolean enable = method.annotation(Enable.class).map(Enable::value).or(true);
        if (method.isAbstract()) {
            final FactoryContext factoryContext = context.first(FactoryContext.class).get();

            final Exceptional<? extends ConstructorContext<?>> constructorCandidate = factoryContext.get(method);
            if (constructorCandidate.present()) {
                final ConstructorContext<?> constructor = constructorCandidate.get();
                return interceptorContext -> this.processInstance(context, (R) constructor.createInstance(interceptorContext.args()).orNull(), enable);
            } else {
                final Factory factory = method.annotation(Factory.class).get();
                if (factory.required()) {
                    throw new IllegalStateException("No factory found for " + method.qualifiedName());
                } else {
                    return interceptorContext -> null;
                }
            }
        }
        else {
            return interceptorContext -> this.processInstance(context, (R) methodContext.method().invoke(interceptorContext.instance(), interceptorContext.args()).orNull(), enable);
        }
    }

    private <T> T processInstance(final ApplicationContext context, final T instance, final boolean enable) {
        try {
            context.get(ComponentPopulator.class).populate(instance);

            if (enable) context.enable(instance);
            return instance;
        } catch (final ApplicationException e) {
            return ExceptionHandler.unchecked(e);
        }
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LAST;
    }
}