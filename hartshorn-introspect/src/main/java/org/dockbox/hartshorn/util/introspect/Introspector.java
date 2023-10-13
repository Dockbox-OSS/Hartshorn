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

package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public interface Introspector extends ReferenceIntrospector {

    <T> TypeView<T> introspect(Class<T> type);

    <T> TypeView<T> introspect(T instance);

    TypeView<?> introspect(Type type);

    TypeView<?> introspect(ParameterizedType type);

    <T> TypeView<T> introspect(GenericType<T> type);

    MethodView<?, ?> introspect(Method method);

    <T> ConstructorView<T> introspect(Constructor<T> method);

    FieldView<?, ?> introspect(Field field);

    ParameterView<?> introspect(Parameter parameter);

    ElementAnnotationsIntrospector introspect(AnnotatedElement annotatedElement);

    AnnotationLookup annotations();
}
