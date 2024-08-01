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

import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides information about the parameters of a method or constructor.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface ExecutableParametersIntrospector {

    /**
     * Returns the types of all parameters of the executable element. If the executable element does not declare any
     * parameters, an empty list is returned. The items in the list are ordered by their index in the executable
     * element.
     *
     * @return the types of all parameters of the executable element
     */
    List<TypeView<?>> types();

    /**
     * Returns the generic types of all parameters of the executable element. If the executable element does not
     * declare any parameters, an empty list is returned. The items in the list are ordered by their index in the
     * executable element.
     *
     * @return the generic types of all parameters of the executable element
     */
    List<TypeView<?>> genericTypes();

    /**
     * Returns the parameters of the executable element. If the executable element does not declare any parameters, an
     * empty list is returned. The items in the list are ordered by their index in the executable element.
     *
     * @return the parameters of the executable element
     */
    List<ParameterView<?>> all();

    /**
     * Returns the parameters of the executable element that are annotated with the provided annotation. If the
     * executable element does not declare any parameters, an empty list is returned.
     *
     * @param annotation the annotation to match
     * @return the parameters of the executable element that are annotated with the provided annotation
     */
    List<ParameterView<?>> annotatedWith(Class<? extends Annotation> annotation);

    /**
     * Returns the parameter at the provided index. If the index is out of bounds, an empty {@link Option} is
     * returned.
     *
     * @param index the index of the parameter
     * @return the parameter at the provided index
     */
    Option<ParameterView<?>> at(int index);

    /**
     * Returns the amount of parameters declared by the executable element.
     *
     * @return the amount of parameters declared by the executable element
     */
    int count();

    /**
     * Returns whether the parameters of the executable element match the provided types. A match is defined as the
     * amount of parameters being equal to the amount of provided types, and each parameter being equal to- or a child
     * of the provided type.
     *
     * @param parameterTypes the types to match
     * @return {@code true} if the parameters of the executable element match the provided types, else {@code false}
     */
    boolean matches(Class<?>... parameterTypes);

    /**
     * Returns whether the parameters of the executable element match the provided types exactly. A match is defined as
     * the amount of parameters being equal to the amount of provided types, and each parameter being equal to the
     * provided type.
     *
     * @param parameterTypes the types to match
     * @return {@code true} if the parameters of the executable element match the provided types exactly, else {@code false}
     */
    boolean matchesExact(Class<?>... parameterTypes);

    /**
     * Returns whether the parameters of the executable element match the provided types. A match is defined as the
     * amount of parameters being equal to the amount of provided types, and each parameter being equal to- or a child
     * of the provided type.
     *
     * @param parameterTypes the types to match
     * @return {@code true} if the parameters of the executable element match the provided types, else {@code false}
     */
    boolean matches(List<Class<?>> parameterTypes);

    /**
     * Returns whether the parameters of the executable element match the provided types exactly. A match is defined as
     * the amount of parameters being equal to the amount of provided types, and each parameter being equal to the
     * provided type.
     *
     * @param parameterTypes the types to match
     * @return {@code true} if the parameters of the executable element match the provided types exactly, else {@code false}
     */
    boolean matchesExact(List<Class<?>> parameterTypes);

}
