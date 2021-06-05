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

import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConstructorWireContext<T, I extends T> implements WireContext<T, I> {

    private final Class<T> contract;
    private final Class<I> implementation;
    private final String name;

    @Override
    public I create(Object... arguments) {
        Class<?>[] argumentTypes = Arrays.stream(arguments).map(Object::getClass).toArray(Class<?>[]::new);
        try {
            Collection<Constructor<I>> constructors = Reflect.annotatedConstructors(this.getImplementation(), Wired.class);
            Constructor<I> ctor = null;
            for (Constructor<I> constructor : constructors) {
                boolean valid = true;
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    Class<?> parameterType = constructor.getParameterTypes()[i];
                    Object argument = arguments[i];
                    if (argument == null) {
                        throw new IllegalArgumentException("Autowired parameters can not be null");
                    }
                    if (!Reflect.assignableFrom(parameterType, argument.getClass())) {
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

            if (ctor.isAnnotationPresent(Wired.class)) {
                return ctor.newInstance(arguments);
            }
            else {
                throw new IllegalArgumentException("Could not autowire " + this.getImplementation().getCanonicalName() + " as the applicable constructor is not marked with @AutoWired");
            }
        }
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Could not autowire " + this.getImplementation().getCanonicalName() + ", no constructor could be accessed", e);
        }
    }
}
