/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Concurrent implementation of {@link IntrospectionViewCache}. This implementation is thread-safe and performs well in
 * batch mode. It is recommended to use this implementation in multi-threaded environments, or when batch mode is enabled.
 *
 * <p>Note that it is not guaranteed that the same instance is returned for concurrent calls to the same method. This is
 * because the cache is not synchronized, and the cache may be overwritten by concurrent calls. This is typically not an
 * issue, as the cache is populated with the same effective value. If this is not the case, it is recommended to use a
 * synchronized cache.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class ConcurrentIntrospectionViewCache implements IntrospectionViewCache {

    private final Map<Class<?>, TypeView<?>> typeViewCache = new ConcurrentHashMap<>();
    private final Map<Method, MethodView<?, ?>> methodViewCache = new ConcurrentHashMap<>();
    private final Map<Field, FieldView<?, ?>> fieldViewCache = new ConcurrentHashMap<>();
    private final Map<Parameter, ParameterView<?>> parameterViewCache = new ConcurrentHashMap<>();
    private final Map<Constructor<?>, ConstructorView<?>> constructorViewCache = new ConcurrentHashMap<>();
    private final Map<Package, PackageView> packageViewCache = new ConcurrentHashMap<>();

    @Override
    public <T> TypeView<T> computeIfAbsent(Class<T> type, Supplier<TypeView<T>> viewSupplier) {
        return TypeUtils.adjustWildcards(this.typeViewCache.computeIfAbsent(type, key0 -> viewSupplier.get()), TypeView.class);
    }

    @Override
    public MethodView<?, ?> computeIfAbsent(Method method, Supplier<MethodView<?, ?>> viewSupplier) {
        return this.methodViewCache.computeIfAbsent(method, key0 -> viewSupplier.get());
    }

    @Override
    public FieldView<?, ?> computeIfAbsent(Field field, Supplier<FieldView<?, ?>> viewSupplier) {
        return this.fieldViewCache.computeIfAbsent(field, key0 -> viewSupplier.get());
    }

    @Override
    public ParameterView<?> computeIfAbsent(Parameter parameter, Supplier<ParameterView<?>> viewSupplier) {
        return this.parameterViewCache.computeIfAbsent(parameter, key0 -> viewSupplier.get());
    }

    @Override
    public <T> ConstructorView<T> computeIfAbsent(Constructor<T> constructor, Supplier<ConstructorView<T>> viewSupplier) {
        return TypeUtils.adjustWildcards(this.constructorViewCache.computeIfAbsent(constructor, key0 -> viewSupplier.get()), ConstructorView.class);
    }

    @Override
    public PackageView computeIfAbsent(Package pkg, Supplier<PackageView> viewSupplier) {
        return this.packageViewCache.computeIfAbsent(pkg, key0 -> viewSupplier.get());
    }
}
