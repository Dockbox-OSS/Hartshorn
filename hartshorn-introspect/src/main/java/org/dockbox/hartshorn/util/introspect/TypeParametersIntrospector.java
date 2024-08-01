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

import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a list of type variables and parameters. This is used to represent the type variables and parameters
 * of a type. This is similar to {@link TypeVariablesIntrospector}, but includes more complex type parameters and
 * references through {@link TypeParameterView}s.
 *
 * <p>Parameter views within this introspector are grouped in two categories: input and output. Input parameters are
 * values provided to type variables, output parameters are values that are provided to parent types. For example, in
 * the following declaration, {@code T} is an input parameter, {@code String} is an output parameter:
 * <pre>{@code
 * public class Foo<T> extends Bar<String> {
 *    // ...
 * }
 * }</pre>
 *
 * <p>If the introspected {@link TypeView} is parameterized (either because it was introspected as a type reference,
 * or as a parameterized parent of another class), the input parameters may also include the provided concrete types.
 * For example, using the declaration above, if the type {@code Foo} is parameterized as {@code Foo<Integer>}, the
 * input parameters will include {@code Integer} as the {@link TypeParameterView#resolvedType() resolved type} of
 * {@code T}.
 *
 * @since 0.4.7
 *
 * @author Guus Lieben
 */
public interface TypeParametersIntrospector {

    /**
     * Returns the input type parameter for the provided index. If the index is out of bounds, an empty {@link Option}
     * is returned.
     *
     * @param index the index of the input type parameter
     * @return the input type parameter for the provided index
     */
    Option<TypeParameterView> atIndex(int index);

    /**
     * Returns all input type parameters for the introspected type. This includes all type parameters that are defined
     * on the introspected type, with optional concrete values if the type is parameterized.
     *
     * @return all input type parameters for the introspected type
     */
    TypeParameterList allInput();

    /**
     * Returns all output type parameters for the introspected type. This includes all type parameters that are defined
     * on all direct parent types of the introspected type. Output parameters may either be concrete types, or type
     * variables.
     *
     * @return all output type parameters for the introspected type
     */
    TypeParameterList allOutput();

    /**
     * Attempts to resolve the concrete type parameters for a given parent type. If the parent type is not a parent
     * of the introspected type, an empty {@link TypeParameterList} is returned.
     *
     * <p>This visits the type hierarchy of the introspected type, and attempts to resolve the concrete type parameters
     * for the provided parent type, even if the type parameters are defined on different levels of the hierarchy. For
     * example, in the following declaration, {@code T} and {@code U} are defined on parent type {@code Bar}:
     * <pre>{@code
     * interface Bar<T, U> {}
     * class Foo<T> implements Bar<T, String> {}
     * class Baz extends Foo<Integer> {}
     * }</pre>
     *
     * <p>When introspecting {@code Baz}, and providing {@code Bar} as the parent type, the returned list will contain
     * {@code T} as {@code Integer} and {@code U} as {@code String}.
     *
     * <p>If the provided type is equal to the introspected type, the result of this method is equal to
     * {@link #allInput()}.
     *
     * @param fromParentType the parent type to resolve the concrete type parameters for
     * @return the concrete type parameters for the provided parent type
     */
    TypeParameterList inputFor(Class<?> fromParentType);

    /**
     * Gets the output type parameters for the provided parent type. This requires the provided type to be a
     * direct parent of the introspected type. If the provided type is not a direct parent, an empty
     * {@link TypeParameterList} is returned. The returned list may contain concrete types, or type variables.
     * To get the concrete type parameters for a parent type, use {@link #inputFor(Class)}.
     *
     * @param fromParentType the parent type to get the output type parameters for
     * @return the output type parameters for the provided parent type
     */
    TypeParameterList outputFor(Class<?> fromParentType);

}
