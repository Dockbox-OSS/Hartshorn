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

package org.dockbox.hartshorn.util.introspect.view;

import java.util.List;

import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A view of a type, which may be a {@link Class}, {@link java.lang.reflect.ParameterizedType}, or any other
 * {@link java.lang.reflect.Type}. This supports various introspection operations to retrieve information about
 * the type.
 *
 * <p>In most cases, this introspector is similar to {@link Class} and related classes, but it provides the
 * available information in a cohesive way, and it supports all types, not just classes. Wildcard types are
 * also supported, and can be introspected.
 *
 * @param <T> the type of the type
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface TypeView<T> extends AnnotatedElementView, ModifierCarrierView {

    /**
     * Returns the raw {@link Class} of the type. No guarantees are made about whether the returned class is
     * already initialized or not. If the type is a wildcard type, this method will return {@link Object}.
     *
     * @return the raw class of the type
     */
    Class<T> type();

    /**
     * Whether this type is a {@link Void} or {@link Void#TYPE primitive void} type.
     *
     * @return {@code true} if this type is a void type, {@code false} otherwise
     */
    boolean isVoid();

    /**
     * Whether this type is an anonymous type. Anonymous types are types that are declared inline, and do
     * not have an explicit name in source code.
     *
     * @return {@code true} if this type is an anonymous type, {@code false} otherwise
     */
    boolean isAnonymous();

    /**
     * Whether this type is a primitive type. Primitive types are the types that are defined in the Java
     * language specification.
     *
     * @return {@code true} if this type is a primitive type, {@code false} otherwise
     *
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Java Primitive Types</a>
     */
    boolean isPrimitive();

    /**
     * Whether this type is a {@link Enum} type.
     *
     * @return {@code true} if this type is an enum type, {@code false} otherwise
     */
    boolean isEnum();

    /**
     * Whether this type is an {@link java.lang.annotation.Annotation} type.
     *
     * @return {@code true} if this type is an annotation type, {@code false} otherwise
     */
    boolean isAnnotation();

    /**
     * Whether this type is an interface.
     *
     * @return {@code true} if this type is an interface, {@code false} otherwise
     */
    boolean isInterface();

    /**
     * Whether this type is a {@link Record} type.
     *
     * @return {@code true} if this type is a record type, {@code false} otherwise
     */
    boolean isRecord();

    /**
     * Whether this type is an array type.
     *
     * @return {@code true} if this type is an array type, {@code false} otherwise
     */
    boolean isArray();

    /**
     * Whether this type is a wildcard type. Wildcard types are used in generic type declarations to
     * specify unknown types.
     *
     * @return {@code true} if this type is a wildcard type, {@code false} otherwise
     */
    boolean isWildcard();

    /**
     * Whether this type is sealed. Sealed types are types that are declared with the {@code sealed}
     * modifier. Sealed types are restricted in the types that can extend them.
     *
     * @return {@code true} if this type is sealed, {@code false} otherwise
     */
    boolean isSealed();

    /**
     * Whether this type is non-sealed. Non-sealed types are types that are declared with the {@code
     * non-sealed} modifier.
     *
     * @return {@code true} if this type is non-sealed, {@code false} otherwise
     */
    boolean isNonSealed();

    /**
     * Whether this type is a permitted subclass of a sealed type. Permitted subclasses are subclasses
     * of a sealed type that are explicitly allowed to extend the sealed type.
     *
     * @return {@code true} if this type is a permitted subclass, {@code false} otherwise
     */
    boolean isPermittedSubclass();

    /**
     * Whether this type is a permitted subclass of the provided sealed type. Permitted subclasses are
     * subclasses of a sealed type that are explicitly allowed to extend the sealed type.
     *
     * @param subclass the sealed type to check
     * @return {@code true} if this type is a permitted subclass of the provided sealed type, {@code false} otherwise
     */
    boolean isPermittedSubclass(Class<?> subclass);

    /**
     * Returns the permitted subclasses of this sealed type. Permitted subclasses are subclasses of a
     * sealed type that are explicitly allowed to extend the sealed type. If this type is not a sealed
     * type, an empty list is returned.
     *
     * @return the permitted subclasses of this sealed type
     */
    List<TypeView<? extends T>> permittedSubclasses();

    /**
     * Whether this type is declared within a package starting with the provided prefix. This method
     * will return {@code true} if the package of this type starts with the provided prefix, or if the
     * package of this type is a subpackage of a package that starts with the provided prefix.
     *
     * @param prefix the prefix to check
     * @return {@code true} if this type is declared within a package starting with the provided prefix, {@code false} otherwise
     */
    boolean isDeclaredIn(String prefix);

    /**
     * Whether the provided instance is an instance of this type.
     *
     * @param object the instance to check
     * @return {@code true} if the provided instance is an instance of this type, {@code false} otherwise
     */
    boolean isInstance(Object object);

    /**
     * Returns the interfaces that are directly implemented by this type. If this type is an interface,
     * this method returns the interfaces that are extended by this interface. If the interfaces are
     * parameterized, this returns the raw type of the interface.
     *
     * @return the interfaces that are directly implemented by this type
     */
    List<TypeView<?>> interfaces();

    /**
     * Returns the interfaces that are directly implemented by this type. If this type is an interface,
     * this method returns the interfaces that are extended by this interface. If the interfaces are
     * parameterized, this returns the parameterized type of the interface.
     *
     * @return the interfaces that are directly implemented by this type
     */
    List<TypeView<?>> genericInterfaces();

    /**
     * Returns the super class of this type. If this type is an interface, primitive, or {@link Object},
     * this method returns a {@link Void} type. If the super class is parameterized, this returns the
     * raw type of the super class.
     *
     * @return the super class of this type
     */
    TypeView<?> superClass();

    /**
     * Returns the super class of this type. If this type is an interface, primitive, or {@link Object},
     * this method returns a {@link Void} type. If the super class is parameterized, this returns the
     * parameterized type of the super class.
     *
     * @return the super class of this type
     */
    TypeView<?> genericSuperClass();

    /**
     * Returns a {@link TypeMethodsIntrospector} for this type. The introspector can be used to retrieve
     * information about the methods declared by this type.
     *
     * @return a {@link TypeMethodsIntrospector} for this type
     */
    TypeMethodsIntrospector<T> methods();

    /**
     * Returns a {@link TypeFieldsIntrospector} for this type. The introspector can be used to retrieve
     * information about the fields declared by this type.
     *
     * @return a {@link TypeFieldsIntrospector} for this type
     */
    TypeFieldsIntrospector<T> fields();

    /**
     * Returns a {@link TypeConstructorsIntrospector} for this type. The introspector can be used to
     * retrieve information about the constructors declared by this type.
     *
     * @return a {@link TypeConstructorsIntrospector} for this type
     */
    TypeConstructorsIntrospector<T> constructors();

    /**
     * Returns a {@link TypeParametersIntrospector} for this type. The introspector can be used to
     * retrieve information about the type parameters of this type.
     *
     * @return a {@link TypeParametersIntrospector} for this type
     */
    TypeParametersIntrospector typeParameters();

    /**
     * Whether this type is a parent of the provided type. This doesn't need to be a direct parent, but
     * can be a parent of a parent, etc.
     *
     * @param type the type to check
     * @return {@code true} if this type is a parent of the provided type, {@code false} otherwise
     */
    boolean isParentOf(Class<?> type);

    /**
     * Whether this type is a child of the provided type. This doesn't need to be a direct child, but
     * can be a child of a child, etc.
     *
     * @param type the type to check
     * @return {@code true} if this type is a child of the provided type, {@code false} otherwise
     */
    boolean isChildOf(Class<?> type);

    /**
     * Whether this type is equal to the provided type. If this type is parameterized, this method will
     * compare the raw type of this type to the provided type.
     *
     * @param type the type to check
     * @return {@code true} if this type is equal to the provided type, {@code false} otherwise
     */
    boolean is(Class<?> type);

    /**
     * Returns the component type of this type. If this type is not an array type, this method returns
     * an empty {@link Option}.
     *
     * @return the component type of this type
     */
    Option<TypeView<?>> elementType();

    /**
     * Returns all constants of this type. If this type is not an enum type, this method returns an
     * empty list.
     *
     * @return all constants of this type
     */
    List<T> enumConstants();

    /**
     * Returns the default value of this type. If this type is not a primitive type, this method returns
     * {@code null}. For primitive types, this method returns the default value of the primitive type. If
     * this type is a primitive wrapper, this method returns the default value of the primitive type.
     *
     * @return the default value of this type
     */
    T defaultOrNull();

    /**
     * Attempts to cast the provided object to this type. If the provided object is not an instance of
     * this type, a {@link ClassCastException} is thrown.
     *
     * @param object the object to cast
     * @return the provided object, cast to this type
     */
    T cast(Object object);

    /**
     * Returns the package of this type.
     *
     * @return the package of this type
     */
    PackageView packageInfo();

    /**
     * Returns the raw type of this type. If this type is not a parameterized type, this method returns
     * the current instance.
     *
     * @return the raw type of this type
     */
    TypeView<?> rawType();
}
