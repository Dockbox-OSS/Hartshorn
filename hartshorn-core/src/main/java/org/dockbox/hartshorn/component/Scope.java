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

package org.dockbox.hartshorn.component;

/**
 * {@link Scope Scopes} can be used to represent a specific scope in which
 * components are registered. Scopes are used to determine which components are
 * available in which context.
 *
 * <p>Implementations of this interface do not need to be thread-safe, as they
 * are not directly responsible for managing the hierarchies of components.
 * Instead, these hierarchies are managed by a {@link ComponentProvider} which
 * is capable of managing scoped component registrations.
 *
 * <p>A scope can be provided to a {@link ComponentKey}, which can then be used
 * by a {@link ComponentProvider} to determine which components are available.
 * It is not ensured individual {@link ComponentProvider}s will support this,
 * except for {@link org.dockbox.hartshorn.application.context.ApplicationContext},
 * which will always support scopes.
 *
 * <p>Scopes can be used to determine which components are available in which
 * context. For example, this may be used to create short-lived singletons for
 * HTTP requests (e.g. for a {@code HttpRequest} and {@code HttpResponse}).
 *
 * <p>Scopes are expected to correctly implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()} to ensure that they can be used as keys in a dictionary
 * of scopes.
 *
 * @see ComponentKey#scope()
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface Scope {

    /**
     * The type of the scope, or a parent scope. This is used to determine which
     * scopes are affected by {@link Scoped}, by allowing subclasses to be
     * installed based on the configuration of a parent scope.
     *
     * @return The type of the scope, or a parent scope.
     */
    ScopeKey installableScopeType();

}
