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

import java.util.List;

import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.provider.Provider;

/**
 * A strategy for selecting the provider with the highest priority from a {@link BindingHierarchy}.
 * If the hierarchy is empty, {@code null} is returned.
 *
 * @see ProviderSelectionStrategy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class HighestPriorityProviderSelectionStrategy implements ProviderSelectionStrategy {

    /**
     * A singleton instance of this strategy. This instance can be used to avoid unnecessary
     * object creation.
     */
    public static final ProviderSelectionStrategy INSTANCE = new HighestPriorityProviderSelectionStrategy();

    @Override
    public <T> Provider<T> selectProvider(BindingHierarchy<T> hierarchy) {
        List<Provider<T>> providers = hierarchy.providers();
        if (providers.isEmpty()) {
            return null;
        }
        return providers.getFirst();
    }
}
