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
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.binding.Bindings;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.FactoryContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;

public class FactoryServiceModifier extends ServiceAnnotatedMethodModifier<Factory, Service> {

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
        if (method.isAbstract()) {
            final FactoryContext factoryContext = context.first(FactoryContext.class).get();
            final ConstructorContext<?> constructor = factoryContext.get(method);
            return (instance, args, proxyContext) -> this.populateAndEnable(context, (R) constructor.createInstance(args).orNull());
        }
        else {
            return (instance, args, proxyContext) -> this.populateAndEnable(context, (R) methodContext.method().invoke(instance, args).orNull());
        }
    }

    private <T> T populateAndEnable(final ApplicationContext context, final T instance) {
        try {
            context.populate(instance);
            Bindings.enable(instance);
            return instance;
        } catch (ApplicationException e) {
            throw e.runtime();
        }
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.LAST;
    }
}
