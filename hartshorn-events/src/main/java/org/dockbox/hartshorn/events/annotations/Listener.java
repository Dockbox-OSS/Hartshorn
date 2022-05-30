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

package org.dockbox.hartshorn.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface to mark a method as an event listener.
 *
 * @see <a href="https://github.com/GuusLieben/Hartshorn/wiki/Events">Events</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {
    /**
     * The priority at which the listener should be called. The higher the priority the earlier the
     * listener will be called.
     *
     * @return the priority
     */
    Priority value() default Priority.NORMAL;

    enum Priority {
        /** Execute the listener after all other listeners are done. */
        LAST(0x14),
        /** Execute the listener after all normal listeners are done. */
        LATE(0xf),
        /** Execute the listener at the regular invocation phase. */
        NORMAL(0xa),
        /** Execute the listener before the normal listeners are called. */
        EARLY(0x5),
        /** Execute the listener before all other listeners are called. */
        FIRST(0x0);

        private final int priority;

        Priority(final int priority) {
            this.priority = priority;
        }

        public int priority() {
            return this.priority;
        }
    }
}
