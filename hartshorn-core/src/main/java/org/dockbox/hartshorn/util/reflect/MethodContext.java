/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.reflect;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Exceptional;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class MethodContext<T, P> extends ExecutableElementContext<Method, P> implements ObtainableElement<T>, TypedElementContext<T> {

    private static final Map<Method, MethodContext<?, ?>> cache = new ConcurrentHashMap<>();

    private static MethodInvoker<?, ?> defaultInvoker = new ReflectionMethodInvoker<>();

    private final Method method;

    private TypeContext<T> returnType;
    private TypeContext<T> genericReturnType;
    private String qualifiedName;

    private MethodInvoker<T, P> invoker;

    public MethodContext(final Method method) {
        this.method = method;
    }

    MethodContext invoker(final MethodInvoker<T, P> invoker) {
        this.invoker = invoker;
        return this;
    }

    public Method method() {
        return this.method;
    }

    public static void defaultInvoker(final MethodInvoker<?, ?> defaultInvoker) {
        MethodContext.defaultInvoker = defaultInvoker;
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

    @Override
    public Exceptional<T> obtain(final ApplicationContext applicationContext) {
        return this.invoke(applicationContext);
    }

    @Override
    public TypeContext<T> type() {
        return this.returnType();
    }

    @Override
    public TypeContext<T> genericType() {
        return this.genericReturnType();
    }
}
