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

import org.dockbox.hartshorn.inject.resolve.ComponentInjectionPoint;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * A strategy that can be used to populate a specific injection point for a component. While strategies are
 * constrained to populating single injection points like methods and fields, strategies may be used by a
 * {@link StrategyComponentPopulator} to populate an entire component.
 *
 * <p>It remains up to the strategy to determine whether- and how to populate the injection point. Additional
 * validation may be applied, and exceptions may be thrown if the injection point cannot be populated.
 *
 * <p>Strategies are expected to be stateless, and may be reused for multiple injection points.
 *
 * @see StrategyComponentPopulator
 * @see PopulateComponentContext
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ComponentPopulationStrategy {

    /**
     * Populates the given injection point. The injection point is described by the given {@link ComponentInjectionPoint},
     * and the context in which the injection point is populated is described by the given {@link PopulateComponentContext}.
     *
     * <p>Implementations are expected to throw an {@link ApplicationException} if the injection point cannot be populated,
     * or if constraints are violated.
     *
     * @param context the context in which the injection point is populated
     * @param injectionPoint the injection point to populate
     * @param <T> the type of the component that is populated
     * @throws ApplicationException if the injection point cannot be populated, or if constraints are violated
     */
    <T> void populate(PopulateComponentContext<T> context, ComponentInjectionPoint<T> injectionPoint) throws ApplicationException;
}
