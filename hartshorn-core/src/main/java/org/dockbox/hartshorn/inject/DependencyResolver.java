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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.context.ContextCarrier;

import java.util.Collection;
import java.util.Set;

/**
 * A dependency resolver is responsible for resolving a collection of {@link DependencyDeclarationContext} instances
 * into a collection of {@link DependencyContext} instances. This is done by visiting the declarations, and resolving
 * any dependencies that are declared by the declarations.
 *
 * <p>Note that the resulting collection of {@link DependencyContext} instances will typically not only contain
 * dependencies of the declarations that were passed to the resolver, but also dependencies of those dependencies.
 *
 * @see DependencyDeclarationContext
 * @see DependencyContext
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DependencyResolver extends ContextCarrier {

    /**
     * Resolves a collection of {@link DependencyDeclarationContext} instances into a collection of {@link
     * DependencyContext} instances.
     *
     * @param containers The collection of declarations to resolve.
     * @return The collection of resolved dependencies.
     * @throws DependencyResolutionException When a dependency cannot be resolved.
     */
    Set<DependencyContext<?>> resolve(Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException;

}
