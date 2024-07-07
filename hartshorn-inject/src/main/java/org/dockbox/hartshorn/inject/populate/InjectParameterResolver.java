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

package org.dockbox.hartshorn.inject.populate;

import org.dockbox.hartshorn.inject.targets.InjectionPoint;

/**
 * A resolver that may be used to resolve a value for an {@link InjectionPoint}. This is commonly used
 * to extend the functionality of a {@link InjectPopulationStrategy}.
 *
 * @see InjectPopulationStrategy
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface InjectParameterResolver {

    /**
     * Indicates whether this resolver accepts the given {@link InjectionPoint}.
     *
     * @param injectionPoint the injection point to check
     * @return {@code true} if this resolver accepts the given injection point, {@code false} otherwise
     */
    boolean accepts(InjectionPoint injectionPoint);

    /**
     * Resolves a value for the given {@link InjectionPoint}. If the resolver could not resolve a value,
     * {@code null} is returned.
     *
     * @param injectionPoint the injection point to resolve a value for
     * @param context the context in which the injection point is resolved
     * @return the resolved value, or {@code null} if the value could not be resolved
     */
    Object resolve(InjectionPoint injectionPoint, PopulateComponentContext<?> context);
}
