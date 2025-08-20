/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing.construction;

import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * A component post constructor is responsible for performing post-construction tasks on a component instance.
 *
 * <p>These tasks are typically defined within the component itself, such as invoking methods annotated with
 * {@link org.dockbox.hartshorn.inject.annotations.OnInitialized}. As such, it is comparable but not equal to a
 * {@link org.dockbox.hartshorn.inject.processing.ComponentPostProcessor}, as post-constructors are for component-level
 * post-construction tasks, while post-processors are for cross-cutting concerns that (potentially) apply to multiple
 * components.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface ComponentPostConstructor {

    /**
     * Performs post-construction on the given instance within the specified scope.
     *
     * @param instance the instance to perform post-construction on
     * @param scope the scope in which the instance is being constructed
     *
     * @param <T> the type of the instance being post-constructed
     *
     * @return the instance after post-construction has been applied
     *
     * @throws ApplicationException if an error occurs during post-construction
     */
    <T> T doPostConstruct(T instance, Scope scope) throws ApplicationException;
}
