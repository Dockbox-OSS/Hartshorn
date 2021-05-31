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

package org.dockbox.selene.server.minecraft.packets;

import org.dockbox.selene.api.exceptions.Except;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a packet instance which can be sent to a {@link
 * PacketReceiver}.
 */
public abstract class Packet {

    /**
     * Gets the type of the native packet type (typically prefixed by {@code net.minecraft}).
     *
     * @return The {@link Class}, or {@code null}.
     */
    public @Nullable Class<?> getNativePacketType() {
        try {
            return this.internalGetPacketType();
        }
        catch (Exception e) {
            Except.handle(e);
            return null;
        }
    }

    protected abstract Class<?> internalGetPacketType() throws ClassNotFoundException;
}
