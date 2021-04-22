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

package org.dockbox.selene.api.events;

import org.dockbox.selene.api.exceptions.CheckedSeleneException;

/**
 * Checked exception which indicates if a event method or listener should be skipped. If the
 * exception is thrown, the method should not be invoked. This is to be respected by the underlying
 * {@link org.dockbox.selene.api.events.EventBus}.
 */
public class SkipEventException extends CheckedSeleneException {
    public SkipEventException() {
        super("Skipping event");
    }
}
