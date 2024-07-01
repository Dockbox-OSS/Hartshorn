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

import java.util.SortedSet;

import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.provider.Provider;

/**
 * A {@link ProviderSelectionStrategy} which selects the first provider with a priority equal to- or
 * higher than the provided minimum priority. If no provider is found, {@code null} is returned.
 *
 * @see ProviderSelectionStrategy
 * @see BindingHierarchy#priorities()
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class MinimumPriorityProviderSelectionStrategy implements ProviderSelectionStrategy {

    private final long minimumPriorityInclusive;

    public MinimumPriorityProviderSelectionStrategy(long minimumPriorityInclusive) {
        this.minimumPriorityInclusive = minimumPriorityInclusive;
    }

    @Override
    public <T> Provider<T> selectProvider(BindingHierarchy<T> hierarchy) {
        // In ascending order, so can iterate until the first provider with a priority equal to- or
        // higher than the minimum
        SortedSet<Integer> priorities = hierarchy.priorities();
        for (Integer priority : priorities) {
            if (priority >= this.minimumPriorityInclusive) {
                return hierarchy.providers().get(priority);
            }
        }
        return null;
    }
}
