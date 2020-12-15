/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.annotations.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The interface to mark a method as a event listener.

 @see <a href="https://github.com/GuusLieben/Selene/wiki/Events">Events</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {
    /**
     The priority at which the listener should be called. The higher the priority the earlier the listener will be
     called.

     @return the priority
     */
    Priority value() default Priority.NORMAL;

    enum Priority {
        /**
         Execute the listener after all other listeners are done.
         */
        LAST(0x14),
        /**
         Execute the listener after all normal listeners are done.
         */
        LATE(0xf),
        /**
         Execute the listener at the regular invocation phase.
         */
        NORMAL(0xa),
        /**
         Execute the listener before the normal listeners are called.
         */
        EARLY(0x5),
        /**
         Execute the listener before all other listeners are called.
         */
        FIRST(0x0);

        private final int priority;

        Priority(int priority) {
            this.priority = priority;
        }

        /**
         Gets priority.

         @return the priority
         */
        public int getPriority() {
            return this.priority;
        }
    }
}
