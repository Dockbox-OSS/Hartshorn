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
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiFunction;

import lombok.Getter;

@SuppressWarnings("unchecked")
public class MethodContext<T, P> extends ExecutableElementContext<Method> implements ModifierCarrier {

    private static final Map<Method, MethodContext<?, ?>> cache = HartshornUtils.emptyConcurrentMap();

    @Getter private final Method method;

    private TypeContext<T> returnType;
    private TypeContext<P> parent;
    private BiFunction<P, Object[], Exceptional<T>> invoker;


    public MethodContext(final Method method) {
        this.method = method;
    }

    public static MethodContext<?, ?> of(final Method method) {
        if (cache.containsKey(method))
            return cache.get(method);

        return new MethodContext<>(method);
    }

    public Exceptional<T> invoke(final P instance, final Object... arguments) {
        if (this.invoker == null) {
            this.invoker = (o, args) -> Exceptional.of(() -> (T) this.method().invoke(o, args));
        }
        return this.invoker.apply(instance, arguments);
    }

    public TypeContext<T> returnType() {
        if (this.returnType == null) {
            this.returnType = (TypeContext<T>) TypeContext.of(this.method().getReturnType());
        }
        return this.returnType;
    }

    public TypeContext<P> parent() {
        if (this.parent == null) {
            this.parent = (TypeContext<P>) TypeContext.of(this.method.getDeclaringClass());
        }
        return this.parent;
    }

    @Override
    protected Method element() {
        return this.method();
    }

    public String name() {
        return this.method().getName();
    }

    public String qualifiedName() {
        return this.method().toGenericString();
    }

    public Exceptional<T> invoke(final ApplicationContext context) {
        final Object[] args = new Object[this.parameterCount()];
        for (int i = 0; i < this.parameterCount(); i++) {
            args[i] = context.get(this.parameterTypes().get(i));
        }
        final P instance = context.get(this.parent());
        return this.invoke(instance, args);
    }
}
