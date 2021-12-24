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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class MethodContext<T, P> extends ExecutableElementContext<Method> {

    private static final Map<Method, MethodContext<?, ?>> cache = new ConcurrentHashMap<>();

    @Setter(AccessLevel.PACKAGE)
    private static MethodInvoker<?, ?> defaultInvoker = new ReflectionMethodInvoker<>();

    @Getter
    private final Method method;

    private TypeContext<T> returnType;
    private TypeContext<T> genericReturnType;
    private TypeContext<P> parent;
    private String qualifiedName;

    @Setter(AccessLevel.PACKAGE)
    private MethodInvoker<T, P> invoker;

    public MethodContext(final Method method) {
        this.method = method;
    }

    public static MethodContext<?, ?> of(final Method method) {
        if (cache.containsKey(method))
            return cache.get(method);

        final MethodContext<Object, Object> context = new MethodContext<>(method);
        cache.put(method, context);
        return context;
    }

    public Exceptional<T> invoke(final P instance, final Object... arguments) {
        if (this.invoker == null) {
            this.invoker = (MethodInvoker<T, P>) defaultInvoker;
        }
        return this.invoker.invoke(this, instance, arguments);
    }

    public Exceptional<T> invoke(final ApplicationContext context, final P instance) {
        final Object[] args = this.arguments(context);
        return this.invoke(instance, args);
    }
    
    public Exceptional<T> invoke(final ApplicationContext context, final Collection<Object> arguments) {
        return this.invoke(context.get(this.parent()), arguments);
    }

    public Exceptional<T> invoke(final P instance, final Collection<Object> arguments) {
        return this.invoke(instance, arguments.toArray());
    }

    public Exceptional<T> invoke(final ApplicationContext context) {
        final Object[] args = this.arguments(context);
        final P instance = context.get(this.parent());
        return this.invoke(instance, args);
    }

    public Exceptional<T> invokeStatic(final Object... arguments) {
        if (this.has(AccessModifier.STATIC)) return this.invoke(null, arguments);
        else return Exceptional.of(new IllegalAccessException("Method is not static"));
    }

    public Exceptional<T> invokeStatic(final Collection<Object> arguments) {
        return this.invokeStatic(arguments.toArray());
    }

    public Exceptional<T> invokeStatic(final ApplicationContext context) {
        final Object[] args = this.arguments(context);
        return this.invokeStatic(args);
    }

    public TypeContext<T> returnType() {
        if (this.returnType == null) {
            this.returnType = (TypeContext<T>) TypeContext.of(this.method().getReturnType());
        }
        return this.returnType;
    }

    public TypeContext<T> genericReturnType() {
        if (this.genericReturnType == null) {
            final Type genericReturnType = this.method().getGenericReturnType();
            if (genericReturnType instanceof Class clazz) this.genericReturnType = TypeContext.of(clazz);
            else this.genericReturnType = TypeContext.of((ParameterizedType) genericReturnType);
        }
        return this.genericReturnType;
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

    public boolean isProtected() {
        return this.has(AccessModifier.PROTECTED);
    }
}
