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
import org.dockbox.hartshorn.di.context.element.ConstructorContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.UseFactory;

import java.util.List;

public class BoundFactoryProvider<C> extends ContextDrivenProvider<C> {

    protected BoundFactoryProvider(final TypeContext<? extends C> context) {
        super(context);
    }

    @Override
    protected Exceptional<C> create(final ApplicationContext context, final Attribute<?>... attributes) {
        final Exceptional<Object[]> factoryArgs = Bindings.lookup(UseFactory.class, attributes);
        if (factoryArgs.absent()) return Exceptional.empty();
        final Object[] arguments = factoryArgs.get();

        ConstructorContext<? extends C> optimal = this.optimalConstructor();
        if (optimal == null) {
            for (final ConstructorContext<? extends C> constructor : this.context().boundConstructors()) {
                boolean valid = true;
                if (constructor.parameterCount() != arguments.length) continue;

                for (int i = 0; i < constructor.parameters().size(); i++) {
                    final Object argument = arguments[i];
                    if (argument == null) continue;

                    final ParameterContext<?> parameter = constructor.parameters().get(i);
                    // If the given argument is not assignable to the parameter, skip to the next constructor
                    if (!TypeContext.of(argument).childOf(parameter.type())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    optimal = constructor;
                    break;
                }
            }
            if (optimal == null) return Exceptional.empty();
        }

        return optimal.createInstance(arguments).map(instance -> (C) instance);
    }

    @Override
    protected Exceptional<? extends ConstructorContext<? extends C>> findOptimalConstructor() {
        final List<? extends ConstructorContext<? extends C>> constructors = this.context().boundConstructors();
        // If there are more we'll depend on the parameters given
        if (constructors.size() == 1) {
            return Exceptional.of(constructors.get(0));
        }
        return Exceptional.empty();
    }
}
