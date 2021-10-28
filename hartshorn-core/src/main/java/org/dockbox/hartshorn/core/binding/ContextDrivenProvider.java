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

package org.dockbox.hartshorn.core.binding;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;

import java.util.List;

import lombok.Getter;

@Getter
public class ContextDrivenProvider<C> implements Provider<C> {

    private final TypeContext<? extends C> context;
    private ConstructorContext<? extends C> optimalConstructor;

    protected ContextDrivenProvider(final TypeContext<? extends C> context) {
        this.context = context;
    }

    @Override
    public final Exceptional<C> provide(final ApplicationContext context, final Attribute<?>... attributes) {
        this.optimalConstructor = this.findOptimalConstructor().orNull();
        return this.create(context, attributes);
    }

    protected Exceptional<? extends ConstructorContext<? extends C>> findOptimalConstructor() {
        ConstructorContext<? extends C> optimalConstructor = null;
        final List<? extends ConstructorContext<? extends C>> constructors = this.context.injectConstructors();
        if (constructors.isEmpty()) {
            final Exceptional<? extends ConstructorContext<? extends C>> defaultConstructor = this.context.defaultConstructor();
            if (defaultConstructor.absent()) return Exceptional.empty();
            else optimalConstructor = defaultConstructor.get();
        } else {

            /*
             An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
             can be satiated at once.
             */
            if (optimalConstructor == null) {
                optimalConstructor = constructors.get(0);
                for (final ConstructorContext<? extends C> constructor : constructors) {
                    if (optimalConstructor.parameterCount() < constructor.parameterCount()) {
                        optimalConstructor = constructor;
                    }
                }
            }
        }
        return Exceptional.of(optimalConstructor);
    }

    protected Exceptional<C> create(final ApplicationContext context, final Attribute<?>... attributes) {
        return Exceptional.of(() -> this.optimalConstructor.createInstance(context).orNull());
    }
}
