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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.BatchCapableIntrospector;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.IntrospectionEnvironment;
import org.dockbox.hartshorn.util.introspect.ConcurrentIntrospectionViewCache;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionConstructorView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionFieldView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionMethodView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionParameterView;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeView;
import org.dockbox.hartshorn.util.introspect.scan.ClassReferenceLoadException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Introspector implementation based on the {@link java.lang.reflect} package. This implementation
 * is typically the default introspector for applications, and provides complete coverage of the
 * entire introspection API.
 *
 * <p>Individual views are cached to prevent the context from leaking across application instances
 * when used in batching or consecutive mode. The cache is not synchronized, as it is not expected
 * that the same type is introspected concurrently. If this is the case, the cache will not be
 * populated multiple times, but the cache will be overwritten. This is typically won't cause any
 * issues, as the cache is populated with the same effective value.
 *
 * <p>While caches are application specific and non-static by default, this implementation is
 * suitable for multi-application environments. If shared caching is desired (e.g. to reduce
 * memory footprint), a shared cache can be enabled by {@link #enableBatchMode(boolean) enabling
 * batch mode}. Note that this will need to be enabled for all applications.
 *
 * <p>This implementation is proxy-aware, meaning that calls to {@link #introspect(Object)} will
 * return the introspection view of the unproxied type. This is done by using the provided
 * {@link ProxyLookup}. Note that {@link #introspect(Type)} and {@link #introspect(Class)} will
 * return the proxy type, to allow for introspection of proxy types.
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
public class ReflectionIntrospector implements BatchCapableIntrospector {

    private static final ConcurrentIntrospectionViewCache SHARED_CACHE = new ConcurrentIntrospectionViewCache();

    private final ConcurrentIntrospectionViewCache viewCache = new ConcurrentIntrospectionViewCache();
    private final IntrospectionEnvironment environment = new ReflectionIntrospectionEnvironment();

    private final ProxyLookup proxyLookup;
    private final AnnotationLookup annotationLookup;

    private boolean batchModeEnabled = false;

    public ReflectionIntrospector(final ProxyLookup proxyLookup, final AnnotationLookup annotationLookup) {
        this.proxyLookup = proxyLookup;
        this.annotationLookup = annotationLookup;
    }

    @Override
    public boolean batchModeEnabled() {
        return batchModeEnabled;
    }

    @Override
    public void enableBatchMode(boolean enable) {
        this.batchModeEnabled = enable;
    }

    protected ConcurrentIntrospectionViewCache viewCache() {
        return this.batchModeEnabled ? SHARED_CACHE : this.viewCache;
    }

    private <T> TypeView<T> voidType() {
        return (TypeView<T>) new ReflectionTypeView<>(this, Void.class);
    }

    @Override
    public <T> TypeView<T> introspect(final Class<T> type) {
        if (type == null) {
            return this.voidType();
        }
        return this.viewCache().computeIfAbsent(type, () -> new ReflectionTypeView<>(this, type));
    }

    @Override
    public <T> TypeView<T> introspect(final T instance) {
        if (instance == null) {
            return this.voidType();
        }
        else if (this.proxyLookup.isProxy(instance)) {
            final Option<Class<T>> unproxied = this.proxyLookup.unproxy(instance);
            return unproxied.present()
                    ? this.introspect(unproxied.get())
                    : this.voidType();
        }
        else {
            return this.introspect((Class<T>) instance.getClass());
        }
    }

    @Override
    public TypeView<?> introspect(final Type type) {
        if (type instanceof Class<?> clazz) {
            return this.introspect(clazz);
        }
        if (type instanceof ParameterizedType parameterizedType) {
            return this.introspect(parameterizedType);
        }
        throw new RuntimeException("Unexpected type " + type);
    }

    @Override
    public TypeView<?> introspect(final ParameterizedType type) {
        // Do not use cache here, as the type is parameterized
        return new ReflectionTypeView<>(this, type);
    }

    @Override
    public <T> TypeView<T> introspect(final GenericType<T> type) {
        final Option<TypeView<T>> view = type.asClass()
                .<Object>map(this::introspect)
                .orCompute(() -> this.introspect(type.type()))
                .adjust(TypeView.class);
        if (view.present()) {
            return view.get();
        }
        return this.voidType();
    }

    @Override
    public TypeView<?> introspect(final String type) {
        try {
            return this.introspect(Class.forName(type, false, Thread.currentThread().getContextClassLoader()));
        }
        catch (final ClassNotFoundException e) {
            return this.voidType();
        }
    }

    @Override
    public TypeView<?> introspect(TypeReference reference) {
        try {
            return this.introspect(reference.getOrLoad());
        }
        catch(ClassReferenceLoadException e) {
            return this.voidType();
        }
    }

    @Override
    public MethodView<?, ?> introspect(final Method method) {
        return this.viewCache().computeIfAbsent(method, () -> new ReflectionMethodView<>(this, method));
    }

    @Override
    public <T> ConstructorView<T> introspect(final Constructor<T> method) {
        return this.viewCache().computeIfAbsent(method, () -> new ReflectionConstructorView<>(this, method));
    }

    @Override
    public FieldView<?, ?> introspect(final Field field) {
        return this.viewCache().computeIfAbsent(field, () -> new ReflectionFieldView<>(this, field));
    }

    @Override
    public ParameterView<?> introspect(final Parameter parameter) {
        return this.viewCache().computeIfAbsent(parameter, () -> new ReflectionParameterView<>(this, parameter));
    }

    @Override
    public ElementAnnotationsIntrospector introspect(final AnnotatedElement annotatedElement) {
        return new ReflectionElementAnnotationsIntrospector(this, annotatedElement);
    }

    @Override
    public IntrospectionEnvironment environment() {
        return this.environment;
    }

    @Override
    public AnnotationLookup annotations() {
        return this.annotationLookup;
    }
}