/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.api.annotations.event;

import org.dockbox.selene.api.events.parents.Cancellable;
import org.dockbox.selene.api.objects.tuple.Tristate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates whether or not to call a event depending on it's cancelled state (if the event is a
 * instance of {@link Cancellable}). There are three options: {@code TRUE} which only calls the
 * listener if the event is cancelled, {@code FALSE} which only calls the listener if the event is
 * not cancelled (default), and {@code UNDEFINED} which calls the listener in either case.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IsCancelled {
  Tristate value() default Tristate.FALSE;
}
