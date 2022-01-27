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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.events.EventBus;

/**
 * Low level event type which can be cancelled, usually this cancellable state is respected by the
 * underlying implementation.
 */
public interface Cancellable extends Event {

    /**
     * Indicates whether the event is currently cancelled
     *
     * @return The cancelled state
     */
    boolean cancelled();

    /**
     * Sets the cancelled state of the event
     *
     * @param cancelled Whether the event should be cancelled
     */
    Cancellable cancelled(boolean cancelled);

    @Override
    @NonNull
    default Cancellable post() {
        this.applicationContext().get(EventBus.class).post(this);
        return this;
    }
}
