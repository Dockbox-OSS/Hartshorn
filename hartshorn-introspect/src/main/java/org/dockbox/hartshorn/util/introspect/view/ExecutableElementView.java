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

package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.util.introspect.ExecutableParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeVariablesIntrospector;

/**
 * Represents a view of an executable element, such as a method or constructor. This view can be
 * used to introspect the element's parameters, type variables, and declaring type.
 *
 * @param <Parent> the type of the element's parent
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
public interface ExecutableElementView<Parent> extends AnnotatedElementView, ModifierCarrierView {

    /**
     * Returns an {@link ExecutableParametersIntrospector} for the element. This introspector
     * can be used to introspect the element's parameters.
     *
     * @return an introspector for the element's parameters
     */
    ExecutableParametersIntrospector parameters();

    /**
     * Returns an {@link TypeVariablesIntrospector} for the element. This introspector
     * can be used to introspect the element's type variables.
     *
     * @return an introspector for the element's type variables
     */
    TypeVariablesIntrospector typeVariables();

    /**
     * Returns the element's declaring type.
     *
     * @return the element's declaring type
     */
    TypeView<Parent> declaredBy();

    /**
     * Returns the element's return type. For methods this is the type of the value that is returned
     * by the method. For constructors, this is the type of the object that is constructed by the
     * constructor.
     *
     * @return the element's return type
     */
    TypeView<?> resultType();
}
