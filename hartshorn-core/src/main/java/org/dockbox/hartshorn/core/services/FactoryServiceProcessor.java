/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.binding.Bindings;
import org.dockbox.hartshorn.core.binding.ContextDrivenProvider;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.FactoryContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.LinkedList;
import java.util.Set;

@AutomaticActivation
public class FactoryServiceProcessor implements ServiceProcessor<Service> {

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.methods(Factory.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final FactoryContext factoryContext = context.first(FactoryContext.class).get();
        for (final MethodContext<?, T> method : type.methods(Factory.class)) {
            final Factory annotation = method.annotation(Factory.class).get();
            Key<?> key = Key.of(method.returnType());
            if (!"".equals(annotation.value())) key = Key.of(method.returnType(), Bindings.named(annotation.value()));

            final Set<TypeContext<?>> types = HartshornUtils.emptySet();
            for (final Provider<?> provider : context.hierarchy(key).providers()) {
                if (provider instanceof ContextDrivenProvider contextDrivenProvider) {
                    types.add(contextDrivenProvider.context());
                }
            }
            if (types.isEmpty()) throw new IllegalStateException("No provider found for " + key);

            ConstructorContext<?> matching = null;
            candidates:
            for (final TypeContext<?> typeContext : types) {
                for (final ConstructorContext<?> constructor : typeContext.boundConstructors()) {
                    final LinkedList<TypeContext<?>> constructorParemeters = constructor.parameterTypes();
                    final LinkedList<TypeContext<?>> methodParameters = method.parameterTypes();
                    if (methodParameters.equals(constructorParemeters)) {
                        matching = constructor;
                        break candidates;
                    }
                }
            }

            if (matching == null) throw new IllegalStateException("No matching bound constructor found for " + key + " with parameters: " + method.parameterTypes());

            factoryContext.register((MethodContext<Object, ?>) method, (ConstructorContext<Object>) matching);
        }
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.FIRST;
    }
}
