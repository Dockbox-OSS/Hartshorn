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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class SupplierAdapterIntrospector implements Introspector {

    private final Supplier<Introspector> delegate;

    public SupplierAdapterIntrospector(Supplier<Introspector> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> TypeView<T> introspect(Class<T> type) {
        return this.delegate.get().introspect(type);
    }

    @Override
    public <T> TypeView<T> introspect(T instance) {
        return this.delegate.get().introspect(instance);
    }

    @Override
    public TypeView<?> introspect(Type type) {
        return this.delegate.get().introspect(type);
    }

    @Override
    public TypeView<?> introspect(ParameterizedType type) {
        return this.delegate.get().introspect(type);
    }

    @Override
    public TypeView<?> introspect(ParameterizableType type) {
        return this.delegate.get().introspect(type);
    }

    @Override
    public <T> TypeView<T> introspect(GenericType<T> type) {
        return this.delegate.get().introspect(type);
    }

    @Override
    public MethodView<?, ?> introspect(Method method) {
        return this.delegate.get().introspect(method);
    }

    @Override
    public <T> ConstructorView<T> introspect(Constructor<T> method) {
        return this.delegate.get().introspect(method);
    }

    @Override
    public FieldView<?, ?> introspect(Field field) {
        return this.delegate.get().introspect(field);
    }

    @Override
    public ParameterView<?> introspect(Parameter parameter) {
        return this.delegate.get().introspect(parameter);
    }

    @Override
    public PackageView introspect(Package pkg) {
        return this.delegate.get().introspect(pkg);
    }

    @Override
    public AnnotatedElementView introspect(AnnotatedElement element) {
        return this.delegate.get().introspect(element);
    }

    @Override
    public AnnotationLookup annotations() {
        return this.delegate.get().annotations();
    }

    @Override
    public TypeView<?> introspect(String type) {
        return this.delegate.get().introspect(type);
    }

    @Override
    public TypeView<?> introspect(TypeReference reference) {
        return this.delegate.get().introspect(reference);
    }

    @Override
    public IntrospectionEnvironment environment() {
        return this.delegate.get().environment();
    }
}
