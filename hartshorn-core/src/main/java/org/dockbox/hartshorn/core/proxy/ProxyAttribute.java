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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import lombok.Getter;
import lombok.Setter;

public final class ProxyAttribute<T, R> implements Attribute<Class<T>> {

    @Getter private final Class<T> value;
    @Getter private final Method target;
    private final ProxyFunction<T, R> delegate;
    private final ProxyHolder holder = new ProxyHolder();

    @Getter @Setter private Phase phase = Phase.OVERWRITE;
    @Setter private boolean overwriteResult = true;
    @Getter @Setter private int priority = 10;

    private ProxyAttribute(final Class<T> type, final Method target, final ProxyFunction<T, R> delegate) {
        this.value = type;
        this.target = target;
        this.delegate = delegate;
    }

    public static <T, R> ProxyAttribute<T, R> of(final TypeContext<T> type, final MethodContext<?, T> target, final BiFunction<T, Object[], R> proxyFunction) {
        return of(type.type(), target.method(), proxyFunction);
    }

    public static <T, R> ProxyAttribute<T, R> of(final Class<T> type, final Method target, final BiFunction<T, Object[], R> proxyFunction) {
        return new ProxyAttribute<>(type, target, (instance, args, proxyContext) -> proxyFunction.apply(instance, args));
    }

    public static <T, R> ProxyAttribute<T, R> of(final TypeContext<T> type, final MethodContext<?, T> target, final BiConsumer<T, Object[]> proxyFunction) {
        return of(type.type(), target.method(), proxyFunction);
    }

    public static <T, R> ProxyAttribute<T, R> of(final Class<T> type, final Method target, final BiConsumer<T, Object[]> proxyFunction) {
        return new ProxyAttribute<>(type, target, (instance, args, proxyContext) -> {
            proxyFunction.accept(instance, args);
            //noinspection ReturnOfNull
            return null;
        });
    }

    public static <T, R> ProxyAttribute<T, R> of(final TypeContext<T> type, final MethodContext<?, T> target, final ProxyFunction<T, R> proxyFunction) {
        return of(type.type(), target.method(), proxyFunction);
    }

    public static <T, R> ProxyAttribute<T, R> of(final Class<T> type, final Method target, final ProxyFunction<T, R> proxyFunction) {
        return new ProxyAttribute<>(type, target, proxyFunction);
    }

    public Class<?> targetClass() {
        return this.target().getDeclaringClass();
    }

    public R delegate(final T instance, final MethodContext<?, ?> proceed, final Object self, final Object... args) {
        this.holder.cancelled(false);
        return this.delegate.delegate(instance, args, new ProxyContextImpl(proceed, this.holder, self));
    }

    public boolean cancelled() {
        return this.holder.cancelled();
    }

    public boolean overwriteResult() {
        return this.overwriteResult && !this.isVoid();
    }

    public boolean isVoid() {
        return Void.TYPE.equals(this.target().getReturnType());
    }
}
