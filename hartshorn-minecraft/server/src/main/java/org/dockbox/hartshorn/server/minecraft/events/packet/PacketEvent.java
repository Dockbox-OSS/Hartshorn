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

package org.dockbox.hartshorn.server.minecraft.events.packet;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.events.AbstractCancellableEvent;
import org.dockbox.hartshorn.api.events.EventBus;
import org.dockbox.hartshorn.server.minecraft.packets.Packet;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * The event fired when the server <b>sends</b> a packet to a player.
 *
 * @param <T> The type of packet being sent/received
 */
public abstract class PacketEvent<T extends Packet> extends AbstractCancellableEvent {

    @Getter
    private final Player target;
    @Getter
    private T packet;
    @Getter
    private boolean isModified;

    public PacketEvent(T packet, Player target) {
        this.packet = packet;
        this.target = target;
    }

    public void packet(T packet) {
        this.isModified = true;
        this.packet = packet;
    }

    @Override
    public @NotNull PacketEvent<T> post() {
        Hartshorn.context().get(EventBus.class).post(this);
        return this;
    }
}
