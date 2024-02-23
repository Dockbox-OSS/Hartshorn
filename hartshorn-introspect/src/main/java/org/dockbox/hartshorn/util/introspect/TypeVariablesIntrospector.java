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

import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * Provides access to the type variables of a type. A type variable is a type that is declared
 * within a executable element declaration. For example, in the following declaration, {@code T}
 * is a type variable with upper bound {@code String}:
 * <pre>{@code
 * public <T extends String> Foo doBar(T baz) {
 *    // ...
 * }
 * }</pre>
 *
 * @see ExecutableElementView#typeVariables()
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface TypeVariablesIntrospector {

    /**
     * Returns the type variable at the provided index. If the index is out of bounds, an empty {@link Option} is
     * returned.
     *
     * @param index the index of the type variable
     * @return the type variable at the provided index
     */
    Option<TypeView<?>> at(int index);

    /**
     * Returns all type variables for the element. If the element does not declare any type variables, an empty list
     * is returned.
     *
     * @return all type variables for the element
     */
    List<TypeView<?>> all();

    /**
     * Returns the amount of type variables in this list.
     *
     * @return the amount of type variables in this list
     */
    int count();

}
