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

package org.dockbox.hartshorn.inject.binding;

/**
 * A binding hierarchy that allows pruning of bindings based on priority. This is useful for
 * bindings that are added to the hierarchy at runtime, and need to be removed at runtime as well.
 *
 * @param <T> The type of type {@code T} that the hierarchy is for.
 *
 * @see BindingHierarchy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface PrunableBindingHierarchy<T> extends BindingHierarchy<T> {

    /**
     * Removes the provider with the given priority from the hierarchy. If no provider with the
     * given priority exists, nothing happens.
     *
     * @param priority The priority of the provider to remove.
     * @return {@code true} if a provider was removed, {@code false} otherwise.
     */
    boolean prune(int priority);

    /**
     * Removes all providers with a priority higher than the given priority. If no providers with
     * a higher priority exist, nothing happens.
     *
     * @param priority The priority to prune above.
     * @return The amount of providers that were removed.
     */
    int pruneAbove(int priority);

    /**
     * Removes all providers with a priority lower than the given priority. If no providers with
     * a lower priority exist, nothing happens.
     *
     * @param priority The priority to prune below.
     * @return The amount of providers that were removed.
     */
    int pruneBelow(int priority);
}
