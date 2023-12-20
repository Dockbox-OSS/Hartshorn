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

package org.dockbox.hartshorn.component.populate.inject;

/**
 * A resolver that determines the name of an {@link InjectionPoint}. This is commonly used to
 * construct a {@link org.dockbox.hartshorn.component.ComponentKey} for the injection point.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface InjectionPointNameResolver {

    /**
     * Resolves the name of the given {@link InjectionPoint}. If the name cannot be resolved,
     * {@code null} is returned.
     *
     * @param injectionPoint the injection point to resolve the name for
     * @return the name of the injection point, or {@code null} if the name cannot be resolved
     */
    String resolve(InjectionPoint injectionPoint);
}
