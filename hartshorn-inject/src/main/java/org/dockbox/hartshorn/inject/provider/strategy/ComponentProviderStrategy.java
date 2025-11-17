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

package org.dockbox.hartshorn.inject.provider.strategy;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Strategy for providing components. This is a chain of responsibility pattern, where each strategy can delegate to the
 * next strategy in the {@link ComponentProviderStrategyChain} if it cannot provide the requested component.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ComponentProviderStrategy {

    /**
     * Attempts to provide a component for the given {@code componentKey} and {@code requestContext}. If this strategy
     * cannot provide the component, it should delegate to the next strategy in the {@code chain}.
     *
     * @param componentKey the component key to resolve
     * @param requestContext the request context for the component resolution
     * @param chain the strategy chain to delegate to
     * @param <T> the type of the component to resolve
     *
     * @return the resolved object container
     *
     * @throws ComponentResolutionException if the component could not be resolved
     * @throws ApplicationException if the component could not be initialized or processed
     */
    <T> ObjectContainer<T> get(
            ComponentKey<T> componentKey,
            ComponentRequestContext requestContext,
            ComponentProviderStrategyChain<T> chain
    ) throws ComponentResolutionException, ApplicationException;
}
