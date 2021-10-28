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

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiFunction;

import lombok.Getter;

public class MethodContext<T, P> extends ExecutableElementContext<Method> implements ModifierCarrier {

    private static final Map<Method, MethodContext<?, ?>> cache = HartshornUtils.emptyConcurrentMap();

    @Getter private final Method method;

    private TypeContext<T> returnType;
    private TypeContext<P> parent;
    private BiFunction<P, Object[], Exceptional<T>> invoker;
    private String qualifiedName;

    public MethodContext(final Method method) {
        this.method = method;
    }

    public static MethodContext<?, ?> of(final Method method) {
        if (cache.containsKey(method))
            return cache.get(method);

        return new MethodContext<>(method);
    }

    public Exceptional<T> invoke(final ApplicationContext context, final Collection<Object> arguments) {
        return this.invoke(context.get(this.parent()), arguments);
    }

    public Exceptional<T> invoke(final P instance, final Collection<Object> arguments) {
        return this.invoke(instance, arguments.toArray());
    }

    public Exceptional<T> invoke(final P instance, final Object... arguments) {
        if (this.invoker == null) {
            this.invoker = (o, args) -> {
                final Exceptional<T> result = Exceptional.of(() -> (T) this.method().invoke(o, args));
                if (result.caught()) {
                    Throwable cause = result.error();
                    if (result.error().getCause() != null) cause = result.error().getCause();
                    return Exceptional.of(result.orNull(), cause);
                }
                return result;
            };
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
        if (this.qualifiedName == null) {
            final StringJoiner j = new StringJoiner(" ");
            final String shortSig = MethodType.methodType(this.method().getReturnType(), this.method().getParameterTypes()).toString();
            final int split = shortSig.lastIndexOf(')') + 1;
            j.add(shortSig.substring(split)).add(this.method().getName() + shortSig.substring(0, split));
            final String k = j.toString();
            this.qualifiedName = this.parent().name() + '#' + k.substring(k.indexOf(' ') + 1);
        }
        return this.qualifiedName;
    }

    public Exceptional<T> invoke(final ApplicationContext context) {
        final Object[] args = this.arguments(context);
        final P instance = context.get(this.parent());
        return this.invoke(instance, args);
    }
}
