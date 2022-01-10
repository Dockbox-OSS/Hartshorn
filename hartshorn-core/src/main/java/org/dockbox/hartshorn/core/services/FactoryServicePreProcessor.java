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

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.binding.ContextDrivenProvider;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.FactoryContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.LinkedList;

@AutomaticActivation
public class FactoryServicePreProcessor implements ServicePreProcessor<Service> {

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return !key.type().methods(Factory.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final FactoryContext factoryContext = context.first(FactoryContext.class).get();

        methods:
        for (final MethodContext<?, T> method : key.type().methods(Factory.class)) {
            final Factory annotation = method.annotation(Factory.class).get();
            Key<?> returnKey = Key.of(method.returnType());
            if (!"".equals(annotation.value())) returnKey = returnKey.name(annotation.value());

            for (final Provider<?> provider : context.hierarchy(returnKey).providers()) {
                if (provider instanceof ContextDrivenProvider contextDrivenProvider) {
                    final TypeContext<?> typeContext = ((ContextDrivenProvider<?>) provider).context();

                    for (final ConstructorContext<?> constructor : typeContext.boundConstructors()) {
                        final LinkedList<TypeContext<?>> constructorParameters = constructor.parameterTypes();
                        final LinkedList<TypeContext<?>> methodParameters = method.parameterTypes();

                        if (methodParameters.equals(constructorParameters)) {
                            factoryContext.register((MethodContext<Object, ?>) method, (ConstructorContext<Object>) constructor);
                            continue methods;
                        }
                    }
                }
            }

            throw new IllegalStateException("No matching bound constructor found for " + returnKey + " with parameters: " + method.parameterTypes());
        }
    }

    @Override
    public ProcessingOrder order() {
        return ProcessingOrder.FIRST;
    }
}
