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

package org.dockbox.hartshorn.server.minecraft.packets;

import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Represents a packet instance which can be sent to a {@link
 * PacketReceiver}.
 */
public abstract class Packet {

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private ApplicationContext context;

    /**
     * Gets the type of the native packet type (typically prefixed by {@code net.minecraft}).
     *
     * @return The {@link Class}, or {@code null}.
     */
    public @Nullable TypeContext<?> nativeType() {
        try {
            return this.internalGetPacketType();
        }
        catch (final Exception e) {
            Except.handle(e);
            return null;
        }
    }

    protected abstract TypeContext<?> internalGetPacketType();
}
