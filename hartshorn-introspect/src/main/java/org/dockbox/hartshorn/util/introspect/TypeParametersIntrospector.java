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

import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public interface TypeParametersIntrospector {

    /**
     * @deprecated use {@link #resolveInputFor(Class)} instead
     * @param fromInterface the interface to resolve the type parameters for
     * @return the list of type parameters
     */
    @Deprecated(forRemoval = true, since = "23.1")
    List<TypeView<?>> from(final Class<?> fromInterface);

    /**
     * @deprecated use {@link #atIndex(int)} instead
     * @param index the index of the type parameter
     * @return the type parameter at the given index
     */
    @Deprecated(forRemoval = true, since = "23.1")
    Option<TypeView<?>> at(int index);

    List<TypeParameterView> resolveInputFor(final Class<?> fromParentType);

    Option<TypeParameterView> atIndex(int index);

    List<TypeParameterView> all();

    List<TypeParameterView> allInput();

    List<TypeParameterView> allOutput();

    int count();

}
