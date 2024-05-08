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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Synchronization wrapper for {@link IntrospectionViewCache}. This implementation is thread-safe, but does not guarantee
 * optimal performance, especially when used in batch mode.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SynchronizedIntrospectionViewCache implements IntrospectionViewCache {

    private final Map<Class<?>, TypeView<?>> typeViewCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<Method, MethodView<?, ?>> methodViewCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<Field, FieldView<?, ?>> fieldViewCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<Parameter, ParameterView<?>> parameterViewCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<Constructor<?>, ConstructorView<?>> constructorViewCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<Package, PackageView> packageViewCache = Collections.synchronizedMap(new HashMap<>());

    @Override
    public <T> TypeView<T> computeIfAbsent(Class<T> type, Supplier<TypeView<T>> viewSupplier) {
        synchronized(this.typeViewCache) {
            return (TypeView<T>) this.typeViewCache.computeIfAbsent(type, key0 -> viewSupplier.get());
        }
    }

    @Override
    public MethodView<?, ?> computeIfAbsent(Method method, Supplier<MethodView<?, ?>> viewSupplier) {
        synchronized(this.methodViewCache) {
            return this.methodViewCache.computeIfAbsent(method, key0 -> viewSupplier.get());
        }
    }

    @Override
    public FieldView<?, ?> computeIfAbsent(Field field, Supplier<FieldView<?, ?>> viewSupplier) {
        synchronized(this.fieldViewCache) {
            return this.fieldViewCache.computeIfAbsent(field, key0 -> viewSupplier.get());
        }
    }

    @Override
    public ParameterView<?> computeIfAbsent(Parameter parameter, Supplier<ParameterView<?>> viewSupplier) {
        synchronized(this.parameterViewCache) {
            return this.parameterViewCache.computeIfAbsent(parameter, key0 -> viewSupplier.get());
        }
    }

    @Override
    public <T> ConstructorView<T> computeIfAbsent(Constructor<T> constructor, Supplier<ConstructorView<T>> viewSupplier) {
        synchronized(this.constructorViewCache) {
            return (ConstructorView<T>) this.constructorViewCache.computeIfAbsent(constructor, key0 -> viewSupplier.get());
        }
    }

    @Override
    public PackageView computeIfAbsent(Package pkg, Supplier<PackageView> viewSupplier) {
        synchronized(this.packageViewCache) {
            return this.packageViewCache.computeIfAbsent(pkg, key0 -> viewSupplier.get());
        }
    }
}
