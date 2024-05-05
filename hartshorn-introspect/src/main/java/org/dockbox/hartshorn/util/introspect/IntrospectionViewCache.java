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
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A cache for {@link TypeView}, {@link MethodView}, {@link FieldView}, {@link ParameterView} and {@link ConstructorView}
 * instances. This cache can be used by {@link Introspector introspectors} to avoid creating multiple instances of the
 * same view, which can happen when introspecting types repeatedly.
 *
 * <p>The implementation may decide to support concurrent access, but this is not required. If concurrent access is
 * supported, it is not guaranteed that the same instance is returned for concurrent calls to the same method. Note that
 * batch mode may affect the behavior of specific implementations.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface IntrospectionViewCache {

    /**
     * Returns the {@link TypeView} for the provided {@link Class}. If the view is not yet cached, the provided
     * {@link Supplier} is used to create a new instance, which is then cached and returned.
     *
     * @param type the type to introspect
     * @param viewSupplier the supplier to create a new view instance
     * @return the cached view instance
     * @param <T> the type of the view
     */
    <T> TypeView<T> computeIfAbsent(Class<T> type, Supplier<TypeView<T>> viewSupplier);

    /**
     * Returns the {@link MethodView} for the provided {@link Method}. If the view is not yet cached, the provided
     * {@link Supplier} is used to create a new instance, which is then cached and returned.
     *
     * @param method the method to introspect
     * @param viewSupplier the supplier to create a new view instance
     * @return the cached view instance
     */
    MethodView<?, ?> computeIfAbsent(Method method, Supplier<MethodView<?, ?>> viewSupplier);

    /**
     * Returns the {@link FieldView} for the provided {@link Field}. If the view is not yet cached, the provided
     * {@link Supplier} is used to create a new instance, which is then cached and returned.
     *
     * @param field the field to introspect
     * @param viewSupplier the supplier to create a new view instance
     * @return the cached view instance
     */
    FieldView<?, ?> computeIfAbsent(Field field, Supplier<FieldView<?, ?>> viewSupplier);

    /**
     * Returns the {@link ParameterView} for the provided {@link Parameter}. If the view is not yet cached, the provided
     * {@link Supplier} is used to create a new instance, which is then cached and returned.
     *
     * @param parameter the parameter to introspect
     * @param viewSupplier the supplier to create a new view instance
     * @return the cached view instance
     */
    ParameterView<?> computeIfAbsent(Parameter parameter, Supplier<ParameterView<?>> viewSupplier);

    /**
     * Returns the {@link ConstructorView} for the provided {@link Constructor}. If the view is not yet cached, the
     * provided {@link Supplier} is used to create a new instance, which is then cached and returned.
     *
     * @param constructor the constructor to introspect
     * @param viewSupplier the supplier to create a new view instance
     * @return the cached view instance
     * @param <T> the type of the view
     */
    <T> ConstructorView<T> computeIfAbsent(Constructor<T> constructor, Supplier<ConstructorView<T>> viewSupplier);

    /**
     * Returns the {@link PackageView} for the provided {@link Package}. If the view is not yet cached, the
     * provided {@link Supplier} is used to create a new instance, which is then cached and returned.
     *
     * @param pkg the package to introspect
     * @param viewSupplier the supplier to create a new view instance
     * @return the cached view instance
     */
    PackageView computeIfAbsent(Package pkg, Supplier<PackageView> viewSupplier);
}
