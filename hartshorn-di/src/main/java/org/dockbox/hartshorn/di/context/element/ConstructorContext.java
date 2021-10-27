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

public class ConstructorContext<T> extends ExecutableElementContext<Constructor<T>> implements TypedElementContext<T> {

    @Getter
    private final Constructor<T> constructor;
    private TypeContext<T> type;
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
        final Object[] args = this.arguments(context);
        return this.invoker.apply(args);
    }

    public TypeContext<T> type() {
        if (this.type == null) this.type = TypeContext.of(this.element().getDeclaringClass());
        return this.type;
    }

    @Override
    public String name() {
        return this.qualifiedName();
    }

    private void prepareHandle() {
        if (this.invoker == null) {
            this.invoker = args -> Exceptional.of(() -> this.constructor.newInstance(args));
        }
    }

    @Override
    protected Constructor<T> element() {
        return this.constructor();
    }

    @Override
    public String qualifiedName() {
        return "Constructor[%s]".formatted(this.type().qualifiedName());
    }
}
