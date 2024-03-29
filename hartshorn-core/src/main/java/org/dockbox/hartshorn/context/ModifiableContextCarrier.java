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

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * A context carrier is a class that can be used to transport an active {@link ApplicationContext},
 * and allows for the attached {@link ApplicationContext} to be changed.
 *
 * @author Guus Lieben
 * @since 0.4.9
 *
 * @see ContextCarrier
 */
public interface ModifiableContextCarrier extends ContextCarrier {

    /**
     * Sets the active {@link ApplicationContext}. Depending on the implementation, additional
     * rules may be applied to the provided {@link ApplicationContext}.
     *
     * @param context The {@link ApplicationContext} to set.
     * @return The current instance.
     */
    ModifiableContextCarrier applicationContext(ApplicationContext context);
}
