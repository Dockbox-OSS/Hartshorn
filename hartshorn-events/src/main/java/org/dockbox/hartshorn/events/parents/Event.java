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

package org.dockbox.hartshorn.events.parents;

import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.events.EventBus;

/** A low level type which is used when subscribing to, posting, or modifying events. */
public interface Event extends Context, ContextCarrier {

    /**
     * Posts the event directly to the implementation of {@link EventBus}, obtained through
     * the active {@link ApplicationContext}.
     *
     * @return Itself
     */
    default Event post() {
        this.applicationContext().get(EventBus.class).post(this);
        return this;
    }

    /**
     * Enhances the event with the given {@link ApplicationContext}, this acts as a setter for the
     * {@link ApplicationContext}.
     *
     * @param context The context to enhance the event with
     * @return Itself, for chaining
     */
    Event with(ApplicationContext context);
}
