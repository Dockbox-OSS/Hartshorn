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

package org.dockbox.hartshorn.di.inject.wired;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.ConstructorContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;

import java.util.Collection;

import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConstructorBoundContext<T, I extends T> implements BoundContext<T, I> {

    private final Key<T> key;
    private final TypeContext<I> implementation;

    @Override
    public String name() {
        final Named named = this.key.named();
        if (named != null) return named.value();
        return "";
    }

    @Override
    public TypeContext<T> contract() {
        return this.key.contract();
    }

    @Override
    public I create(final ApplicationContext context, final Object... arguments) throws ApplicationException {
        final Collection<ConstructorContext<I>> constructors = this.implementation().boundConstructors();
        ConstructorContext<I> ctor = null;
        for (final ConstructorContext<I> constructor : constructors) {
            if (constructor.parameterCount() != arguments.length) continue;
            boolean valid = true;
            for (int i = 0; i < constructor.parameters().size(); i++) {
                final TypeContext<?> parameterType = constructor.parameterTypes().get(i);
                final Object argument = arguments[i];
                if (argument == null) {
                    throw new IllegalArgumentException("Autowired parameters can not be null");
                }
                if (!TypeContext.of(argument).childOf(parameterType)) {
                    valid = false;
                }
            }
            if (valid) {
                ctor = constructor;
                break;
            }
        }

        if (ctor == null) {
            throw new ApplicationException("Available constructors do not meet expected parameter types");
        }

        final Exceptional<Bound> annotation = ctor.annotation(Bound.class);
        if (annotation.present()) {
            return ctor.createInstance(arguments).orNull();
        }
        else {
            throw new ApplicationException("Could not autowire " + this.implementation().qualifiedName() + " as the applicable constructor is not marked with @AutoWired");
        }
    }
}
