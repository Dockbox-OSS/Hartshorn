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

package org.dockbox.hartshorn.inject.provider.selection;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.Provider;

/**
 * A strategy for selecting a provider from a {@link BindingHierarchy}. Strategies can be
 * provided to {@link ComponentKey}s to suggest how a {@link ComponentProvider} should select
 * a provider from a hierarchy. It is up to the {@link ComponentProvider} to decide whether
 * to respect the strategy or not.
 *
 * <p>Strategies are expected to be stateless, and are therefore expected to be thread-safe.
 * A strategy may only contain state that is provided to it at construction time, like a
 * priority filter.
 *
 * @see MaximumPriorityProviderSelectionStrategy
 * @see MinimumPriorityProviderSelectionStrategy
 * @see HighestPriorityProviderSelectionStrategy
 * @see ExactPriorityProviderSelectionStrategy
 * @see ComponentKey#strategy()
 * @see ComponentKey.Builder#strategy(ProviderSelectionStrategy)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ProviderSelectionStrategy {

    /**
     * Selects a provider from the provided hierarchy, or returns {@code null} if no provider
     * could be selected.
     *
     * @param hierarchy the hierarchy from which to select a provider
     * @return the selected provider, or {@code null} if no provider could be selected
     * @param <T> the type of the provider
     */
    <T> Provider<T> selectProvider(BindingHierarchy<T> hierarchy);
}
