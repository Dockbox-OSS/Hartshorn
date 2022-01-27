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

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.FactoryContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;

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
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final MethodContext<?, T> method = methodContext.method();
        final boolean enable = method.annotation(Enable.class).map(Enable::value).or(true);
        if (method.isAbstract()) {
            final FactoryContext factoryContext = context.first(FactoryContext.class).get();
            final ConstructorContext<?> constructor = factoryContext.get(method);
            return (instance, args, proxyContext) -> this.processInstance(context, (R) constructor.createInstance(args).orNull(), enable);
        }
        else {
            return (instance, args, proxyContext) -> this.processInstance(context, (R) methodContext.method().invoke(instance, args).orNull(), enable);
        }
    }

    private <T> T processInstance(final ApplicationContext context, final T instance, final boolean enable) {
        try {
            context.populate(instance);
            if (enable) context.enable(instance);
            return instance;
        } catch (final ApplicationException e) {
            return ExceptionHandler.unchecked(e);
        }
    }

    @Override
    public ProcessingOrder order() {
        return ProcessingOrder.LAST;
    }
}
