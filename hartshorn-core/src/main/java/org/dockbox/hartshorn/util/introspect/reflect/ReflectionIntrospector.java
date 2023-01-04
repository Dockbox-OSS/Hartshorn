/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.IntrospectionEnvironment;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionConstructorView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionFieldView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionMethodView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionParameterView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeView;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionIntrospector implements Introspector {

    // Caches are not static, as application context is passed to views and associated introspectors.
    // Making these static would cause the context to leak across instances (when used in batching or
    // consecutive mode).
    private final Map<Class<?>, TypeView<?>> typeViewCache = new ConcurrentHashMap<>();
    private final Map<Method, MethodView<?, ?>> methodViewCache = new ConcurrentHashMap<>();
    private final Map<Field, FieldView<?, ?>> fieldViewCache = new ConcurrentHashMap<>();
    private final Map<Parameter, ParameterView<?>> parameterViewCache = new ConcurrentHashMap<>();
    private final Map<Constructor<?>, ConstructorView<?>> constructorViewCache = new ConcurrentHashMap<>();
    private final IntrospectionEnvironment environment = new ReflectionIntrospectionEnvironment();

    private final ApplicationContext applicationContext;

    public ReflectionIntrospector(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private <T> TypeView<T> voidType() {
        return (TypeView<T>) new ReflectionTypeView<>(this, Void.class);
    }

    @Override
    public <T> TypeView<T> introspect(final Class<T> type) {
        if (type == null) {
            return this.voidType();
        }
        if (this.typeViewCache.containsKey(type))
            return (TypeView<T>) this.typeViewCache.get(type);

        final TypeView<T> context = new ReflectionTypeView<>(this, type);
        this.typeViewCache.put(type, context);
        return context;
    }

    @Override
    public <T> TypeView<T> introspect(final T instance) {
        if (instance == null) {
            return this.voidType();
        }
        else if (this.applicationContext().environment().isProxy(instance)) {
            final Class<T> unproxied = this.applicationContext().environment().unproxy(instance);
            return this.introspect(unproxied);
        }
        else {
            return this.introspect((Class<T>) instance.getClass());
        }
    }

    @Override
    public TypeView<?> introspect(final Type type) {
        if (type instanceof Class<?> clazz) return this.introspect(clazz);
        if (type instanceof ParameterizedType parameterizedType) return this.introspect(parameterizedType);
        throw new RuntimeException("Unexpected type " + type);
    }

    @Override
    public TypeView<?> introspect(final ParameterizedType type) {
        // Do not use cache here, as the type is parameterized
        return new ReflectionTypeView<>(this, type);
    }

    @Override
    public TypeView<?> introspect(final String type) {
        try {
            return this.introspect(Class.forName(type));
        }
        catch (final ClassNotFoundException e) {
            return this.voidType();
        }
    }

    @Override
    public MethodView<?, ?> introspect(final Method method) {
        return this.methodViewCache.computeIfAbsent(method, _0 -> new ReflectionMethodView<>(this, method));
    }

    @Override
    public <T> ConstructorView<T> introspect(final Constructor<T> method) {
        return (ConstructorView<T>) this.constructorViewCache.computeIfAbsent(method, _0 -> new ReflectionConstructorView<>(this, method));
    }

    @Override
    public FieldView<?, ?> introspect(final Field field) {
        return this.fieldViewCache.computeIfAbsent(field, _0 -> new ReflectionFieldView<>(this, field));
    }

    @Override
    public ParameterView<?> introspect(final Parameter parameter) {
        return this.parameterViewCache.computeIfAbsent(parameter, _0 -> new ReflectionParameterView<>(this, parameter));
    }

    @Override
    public ElementAnnotationsIntrospector introspect(final AnnotatedElement annotatedElement) {
        return new ReflectionElementAnnotationsIntrospector(this, annotatedElement);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public IntrospectionEnvironment environment() {
        return this.environment;
    }
}
