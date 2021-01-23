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

package org.dockbox.selene.core.proxy;

/**
 * The type passed into proxy functions. Cancellability is unused internally, but may be used by functions.
 */
public class ProxyHolder {

    private boolean cancelled;

    /**
     * {@code true} if the holder is cancelled, otherwise {@code false}.
     *
     * @return {@code true} if the holder is cancelled.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Cancels the holder. Internally this is ignored, and only provides interoperability between different proxy
     * functions.
     *
     * @param cancelled
     *         Whether the holder should be cancelled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
