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

/**
 * Represents a view of a method- or constructor parameter. This view can be used to introspect
 * the parameter's annotations, as well as its name and type.
 *
 * @param <T> the type of the parameter
 *
 * @since 0.4.7
 *
 * @author Guus Lieben
 */
public interface ParameterView<T> extends AnnotatedGenericTypeView<T> {

    /**
     * Returns {@code true} if this parameter represents a variable argument list, or {@code false}
     * otherwise.
     *
     * @return {@code true} if this parameter represents a variable argument list, or {@code false}
     */
    boolean isVarArgs();

    /**
     * Returns true if the parameter has a name according to the class file, or {@code false}
     * otherwise. Whether a parameter has a name is determined by the {@literal MethodParameters}
     * attribute of the method which declares the parameter.
     *
     * @return {@code true} if the parameter has a name according to the class file.
     */
    boolean isNamePresent();

    /**
     * Returns the executable element that declares this parameter. This can be either a method or
     * a constructor.
     *
     * @return the executable element that declares this parameter
     */
    ExecutableElementView<?> declaredBy();
}
