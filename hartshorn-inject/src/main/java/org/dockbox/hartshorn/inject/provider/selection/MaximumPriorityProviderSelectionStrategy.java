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
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;

/**
 * A {@link ProviderSelectionStrategy} which selects the provider with the highest priority, as long
 * as that priority is lower than the provided maximum priority. If no provider is found, {@code null}
 * is returned.
 *
 * @see ProviderSelectionStrategy
 * @see BindingHierarchy#priorities()
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class MaximumPriorityProviderSelectionStrategy implements ProviderSelectionStrategy {

    private final long maximumPriorityExclusive;

    public MaximumPriorityProviderSelectionStrategy(long maximumPriorityExclusive) {
        this.maximumPriorityExclusive = maximumPriorityExclusive;
    }

    @Override
    public <T> InstantiationStrategy<T> selectProvider(BindingHierarchy<T> hierarchy) {
        // In ascending order, so need to iterate backwards until the first provider with a priority
        // lower than the maximum
        SortedSet<Integer> priorities = hierarchy.priorities();
        for (Integer priority : priorities.reversed()) {
            if (priority < this.maximumPriorityExclusive) {
                return hierarchy.get(priority)
                        .orElseThrow(() -> new IllegalStateException("No provider found for priority " + priority + ", but priority was reported."));
            }
        }
        return null;
    }
}
