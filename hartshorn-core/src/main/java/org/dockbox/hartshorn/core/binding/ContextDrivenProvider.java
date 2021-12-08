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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.List;

import lombok.Getter;

/**
 * A {@link ContextDrivenProvider} is a {@link Provider} that uses a {@link ConstructorContext} to
 * create a new instance of a class. The constructor is looked up based on its parameters, where the
 * constructor with the most parameters is chosen in order to satisfy as many dependencies as possible.
 *
 * <p>If no injectable constructors can be found, the default constructro is used instead. If this
 * constructor is not injectable, an {@link IllegalStateException} is thrown.
 *
 * @param <C> The type of the class to create.
 *
 * @author Guus Lieben
 * @since 4.1.2
 * @see Provider
 * @see SupplierProvider
 * @see InstanceProvider
 */
@Getter
public class ContextDrivenProvider<C> implements Provider<C> {

    private final TypeContext<? extends C> context;
    private ConstructorContext<? extends C> optimalConstructor;

    protected ContextDrivenProvider(final TypeContext<? extends C> context) {
        this.context = context;
    }

    @Override
    public final Exceptional<C> provide(final ApplicationContext context) {
        this.optimalConstructor = this.findOptimalConstructor().orNull();
        return this.create(context);
    }

    protected Exceptional<? extends ConstructorContext<? extends C>> findOptimalConstructor() {
        if (this.context().isAbstract()) return Exceptional.empty();
        if (this.optimalConstructor == null) {
            final List<? extends ConstructorContext<? extends C>> constructors = this.context().injectConstructors();
            if (constructors.isEmpty()) {
                final Exceptional<? extends ConstructorContext<? extends C>> defaultConstructor = this.context().defaultConstructor();
                if (defaultConstructor.absent()) {
                    throw new IllegalStateException("No injectable constructors found for " + this.context().type());
                }
                else this.optimalConstructor = defaultConstructor.get();
            } else {

            /*
             An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
             can be satiated at once.
             */
                this.optimalConstructor = constructors.get(0);
                for (final ConstructorContext<? extends C> constructor : constructors) {
                    if (this.optimalConstructor.parameterCount() < constructor.parameterCount()) {
                        this.optimalConstructor = constructor;
                    }
                }
            }
        }
        return Exceptional.of(this.optimalConstructor);
    }

    protected Exceptional<C> create(final ApplicationContext context) {
        if (this.optimalConstructor() == null) return Exceptional.empty();
        return this.optimalConstructor().createInstance(context).rethrowUnchecked().map(instance -> (C) instance);
    }
}
