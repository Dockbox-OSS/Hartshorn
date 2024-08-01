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

import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a list of type parameters. This is used to represent the type parameters of a type, or a method.
 *
 * @see TypeParameterView
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface TypeParameterList extends Iterable<TypeParameterView> {

    /**
     * Returns the type parameter at the provided index. If the index is out of bounds, an empty {@link Option} is
     * returned.
     *
     * @param index the index of the type parameter
     * @return the type parameter at the provided index
     */
    Option<TypeParameterView> atIndex(int index);

    /**
     * Returns the amount of type parameters in this list.
     *
     * @return the amount of type parameters in this list
     */
    int count();

    /**
     * Returns whether this list is empty.
     *
     * @return {@code true} if this list is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Transforms this list to a {@link List} of {@link TypeParameterView}s. The returned list is immutable.
     *
     * @return a {@link List} of {@link TypeParameterView}s
     */
    List<TypeParameterView> asList();

    /**
     * Returns a bi-directional map of type parameters. Depending on whether the type parameter is an input or output
     * parameter, the map will contain different mappings.
     *
     * <p>If the parameter is an input parameter, the map will contain the parameter as the key, and all the types it
     * represents as the values. The represented types will be resolved to their output parameters, similar to
     * {@link TypeParameterView#represents()}. If a type does not represent any outputs, it will not be included in the
     * map.
     *
     * <p>If the parameter is an output parameter, the map will contain the parameter as the key, and the input
     * parameter on the target type as the value. The input parameter will be resolved similar to
     * {@link TypeParameterView#asInputParameter()}.
     *
     * @return a bi-directional map of type parameters
     */
    BiMultiMap<TypeParameterView, TypeParameterView> asMap();

    /**
     * Transforms this list to a {@link Stream} of {@link TypeParameterView}s.
     *
     * @return a {@link Stream} of {@link TypeParameterView}s
     */
    Stream<TypeParameterView> stream();
}
