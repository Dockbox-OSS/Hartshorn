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
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConstructorBoundContext<T, I extends T> implements BoundContext<T, I> {

    private final Key<T> key;
    private final Class<I> implementation;

    @Override
    public String name() {
        final Named named = this.key.named();
        if (named != null) return named.value();
        return "";
    }

    @Override
    public Class<T> contract() {
        return this.key.contract();
    }

    @Override
    public I create(final Object... arguments) {
        final Class<?>[] argumentTypes = Arrays.stream(arguments).map(Object::getClass).toArray(Class<?>[]::new);
        try {
            final Collection<Constructor<I>> constructors = Reflect.constructors(this.implementation(), Bound.class);
            Constructor<I> ctor = null;
            for (final Constructor<I> constructor : constructors) {
                if (constructor.getParameterTypes().length != arguments.length) continue;
                boolean valid = true;
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = constructor.getParameterTypes()[i];
                    final Object argument = arguments[i];
                    if (argument == null) {
                        throw new IllegalArgumentException("Autowired parameters can not be null");
                    }
                    if (!Reflect.assigns(parameterType, argument.getClass())) {
                        valid = false;
                    }
                }
                if (valid){
                    ctor = constructor;
                    break;
                }
            }

            if (ctor == null) {
                throw new NoSuchMethodException("Available constructors do not meet expected parameter types");
            }

            final Exceptional<Bound> annotation = Reflect.annotation(ctor, Bound.class);
            if (annotation.present()) {
                return ctor.newInstance(arguments);
            }
            else {
                throw new IllegalArgumentException("Could not autowire " + this.implementation().getCanonicalName() + " as the applicable constructor is not marked with @AutoWired");
            }
        }
        catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Could not autowire " + this.implementation().getCanonicalName() + ", no constructor could be accessed", e);
        }
    }
}
