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

package org.dockbox.hartshorn.di.context.element;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import lombok.Getter;

public class ConstructorContext<T> extends ExecutableElementContext<Constructor<T>> {

    @Getter
    private final Constructor<T> constructor;
    private Function<Object[], Exceptional<T>> invoker;

    private ConstructorContext(final Constructor<T> constructor) {
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    public static <T> ConstructorContext<T> of(final Constructor<T> constructor) {
        return new ConstructorContext<>(constructor);
    }

    public Exceptional<T> createInstance(final Object... args) {
        this.prepareHandle();
        return this.invoker.apply(args);
    }

    public Exceptional<T> createInstance(final ApplicationContext context) {
        this.prepareHandle();
        final Object[] args = new Object[this.parameters().size()];
        for (int i = 0; i < this.parameters().size(); i++) {
            args[i] = context.get(this.parameters().get(i).type());
        }
        return this.invoker.apply(args);
    }

    private void prepareHandle() {
        if (this.invoker == null) {
//            try {
//                final MethodHandle handle = MethodHandles.lookup().unreflectConstructor(this.constructor);
//                this.invoker = args -> Exceptional.of(() -> {
//                    try {
//                        return (T) handle.invokeExact(args);
//                    }
//                    catch (final Throwable e) {
//                        throw new ApplicationException(e);
//                    }
//                });
//            }
//            catch (final IllegalAccessException e) {
                this.invoker = args -> Exceptional.of(() -> this.constructor.newInstance(args));
//            }
        }
    }

    @Override
    protected Constructor<T> element() {
        return this.constructor();
    }
}
