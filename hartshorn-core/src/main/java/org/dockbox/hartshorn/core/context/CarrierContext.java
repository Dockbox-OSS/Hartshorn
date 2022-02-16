/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.domain.Exceptional;

/**
 * A carrier context is a context that is bound to an active {@link ApplicationContext}.
 */
public interface CarrierContext extends Context, ContextCarrier {
    /**
     * Requests the first available context of the given type. If there is no such context, the active
     * {@link ApplicationContext} is used to create a new context, if the type is annotated with {@link AutoCreating}.
     * If the context does not exist, or the type is not annotated with {@link AutoCreating}, an empty
     * {@link Exceptional} is returned.
     *
     * @param context Type The type of the context to request.
     * @param <C> Type The type of the context to request.
     * @return The requested context.
     */
    <C extends Context> Exceptional<C> first(final Class<C> context);
}
