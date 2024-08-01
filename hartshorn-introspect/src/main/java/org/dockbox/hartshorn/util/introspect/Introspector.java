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

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * An introspector is a utility that can be used to introspect types, methods, fields, parameters,
 * and constructors. This can be used to retrieve information about the introspected element, such
 * as its name, annotations, or modifiers.
 *
 * <p>Within the scope of an application, introspectors can be used to gain insight into the items
 * that are available to the application. This can be used to implement features such as dependency
 * injection, or to implement custom serialization and deserialization.
 *
 * <p>Introspectors provide immutable views of the introspected elements. Executable elements, such
 * as methods and constructors, can be usually be invoked through the introspector. This is not
 * always possible, as some elements may not be accessible to the application. In such cases, the
 * implementation may decide how to proceed.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface Introspector extends ReferenceIntrospector {

    /**
     * Returns a {@link TypeView} for the provided type. This will be based on the provided type's
     * raw signature, and will not include any concrete input type parameters. Type variables and
     * output type parameters will be included if applicable.
     *
     * @param type the type to introspect
     * @param <T>  the type of the provided type
     *
     * @return a view of the provided type
     *
     * @see TypeParameterView for more information on type parameters and type variables
     */
    <T> TypeView<T> introspect(Class<T> type);

    /**
     * Returns a {@link TypeView} for the provided instance. This will be based on the provided
     * instance's type. If the given instance is proxied it may be unproxied automatically. Note
     * that if you wish to introspect the proxy type, you should use {@link #introspect(Class)}
     * directly with {@link Object#getClass()}.
     *
     * @param instance the instance to introspect
     * @param <T> the type of the provided instance
     *
     * @return a view of the provided instance
     */
    <T> TypeView<T> introspect(T instance);

    /**
     * Returns a {@link TypeView} for the provided type. This will be based on the provided type's
     * implementation, which may or may not include concrete input type parameters. Type variables
     * and output type parameters will be included if applicable.
     *
     * @param type the type to introspect
     *
     * @return a view of the provided type
     *
     * @see TypeParameterView for more information on type parameters and type variables
     */
    TypeView<?> introspect(Type type);

    /**
     * Returns a {@link TypeView} for the provided type. This will be based on the provided type's
     * generic signature, and will include all input type parameters (both variable and concrete).
     * Output type parameters will be included if applicable.
     *
     * @param type the type to introspect
     *
     * @return a view of the provided type
     *
     * @see TypeParameterView for more information on type parameters and type variables
     */
    TypeView<?> introspect(ParameterizedType type);

    /**
     * Returns a {@link TypeView} for the provided type. This will be based on the provided type's
     * generic signature, and will include all input type parameters (both variable and concrete).
     * Output type parameters will be included if applicable.
     *
     * @param type the type to introspect
     *
     * @return a view of the provided type
     *
     * @see TypeParameterView for more information on type parameters and type variables
     */
    TypeView<?> introspect(ParameterizableType type);

    /**
     * Returns a {@link TypeView} for the provided type. This will be based on the provided type's
     * generic signature, and will include all input type parameters (both variable and concrete).
     * Output type parameters will be included if applicable.
     *
     * @param type the type to introspect
     * @param <T> the type of the provided type
     *
     * @return a view of the provided type
     */
    <T> TypeView<T> introspect(GenericType<T> type);

    /**
     * Returns a {@link MethodView} for the provided method. This will be based on the provided
     * method's signature, and will include all relevant method metadata.
     *
     * @param method the method to introspect
     *
     * @return a view of the provided method
     */
    MethodView<?, ?> introspect(Method method);

    /**
     * Returns a {@link ConstructorView} for the provided constructor. This will be based on the
     * provided constructor's signature, and will include all relevant constructor metadata.
     *
     * @param method the constructor to introspect
     * @param <T> the type of the provided constructor
     *
     * @return a view of the provided constructor
     */
    <T> ConstructorView<T> introspect(Constructor<T> method);

    /**
     * Returns a {@link FieldView} for the provided field. This will be based on the provided
     * field's signature, and will include all relevant field metadata.
     *
     * @param field the field to introspect
     *
     * @return a view of the provided field
     */
    FieldView<?, ?> introspect(Field field);

    /**
     * Returns a {@link ParameterView} for the provided parameter. This will be based on the
     * provided parameter's signature, and will include all relevant parameter metadata.
     *
     * @param parameter the parameter to introspect
     *
     * @return a view of the provided parameter
     */
    ParameterView<?> introspect(Parameter parameter);

    /**
     * Returns a {@link PackageView} for the provided package. This will be based on the provided
     * package's signature, and will include all relevant package metadata.
     *
     * @param pkg the package to introspect
     *
     * @return a view of the provided package
     */
    PackageView introspect(Package pkg);

    /**
     * Returns a {@link AnnotatedElementView} for the provided element. This will automatically
     * determine the type of the provided element, and return the appropriate view. If the provided
     * element is not supported, a {@link TypeView} of {@link Void} is returned.
     *
     * @param element the element to introspect
     *
     * @return a view of the provided element
     */
    AnnotatedElementView introspect(AnnotatedElement element);

    /**
     * Returns the {@link AnnotationLookup} for this introspector. This can be used to retrieve
     * annotations from introspectable elements.
     *
     * @return the annotation lookup
     */
    AnnotationLookup annotations();
}
