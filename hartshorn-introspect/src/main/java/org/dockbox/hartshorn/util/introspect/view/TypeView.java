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

import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public interface TypeView<T> extends AnnotatedElementView, ModifierCarrierView {

    Class<T> type();

    boolean isVoid();

    boolean isAnonymous();

    boolean isPrimitive();

    boolean isEnum();

    boolean isAnnotation();

    boolean isInterface();

    boolean isRecord();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isAbstract()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isAbstract();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isFinal()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isFinal();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isStatic()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isStatic();

    boolean isArray();

    boolean isWildcard();

    boolean isSealed();

    boolean isNonSealed();

    boolean isPermittedSubclass();

    boolean isPermittedSubclass(Class<?> subclass);

    List<TypeView<? extends T>> permittedSubclasses();

    boolean isDeclaredIn(String prefix);

    boolean isInstance(Object object);

    List<TypeView<?>> interfaces();

    List<TypeView<?>> genericInterfaces();

    TypeView<?> superClass();

    TypeView<?> genericSuperClass();

    TypeMethodsIntrospector<T> methods();

    TypeFieldsIntrospector<T> fields();

    TypeConstructorsIntrospector<T> constructors();

    TypeParametersIntrospector typeParameters();

    boolean isParentOf(Class<?> type);

    boolean isChildOf(Class<?> type);

    boolean is(Class<?> type);

    Option<TypeView<?>> elementType() throws IllegalArgumentException;

    List<T> enumConstants();

    T defaultOrNull();

    T cast(Object object);

    @SuppressWarnings("unchecked")
    default <S extends T> TypeView<S> adjustWildcards() {
        return (TypeView<S>) this;
    }

    PackageView packageInfo();

    TypeView<?> rawType();

}
