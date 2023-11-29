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

public interface TypeParameterList extends Iterable<TypeParameterView> {

    Option<TypeParameterView> atIndex(int index);

    int count();

    boolean isEmpty();

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

    Stream<TypeParameterView> stream();

}
