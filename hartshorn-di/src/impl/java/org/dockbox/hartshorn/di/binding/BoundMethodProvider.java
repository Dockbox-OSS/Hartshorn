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

package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.UseFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BoundMethodProvider<C> implements Provider<C> {

    @Getter
    private MethodContext<C, Object> method;

    @Override
    public Exceptional<C> provide(final ApplicationContext context, final Attribute<?>... attributes) {
        final Exceptional<Object[]> factoryArgs = Bindings.lookup(UseFactory.class, attributes);
        if (factoryArgs.absent()) return Exceptional.empty();
        final Object[] arguments = factoryArgs.get();

        if (arguments.length != this.method().parameterCount()) return Exceptional.empty();
        final Object instance = context.get(this.method().parent());
        return this.method().invoke(instance, arguments);
    }
}
