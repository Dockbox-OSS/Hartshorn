/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
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
public class FactoryServicePostProcessor extends ServiceAnnotatedMethodPostProcessor<Factory, Service> {

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
