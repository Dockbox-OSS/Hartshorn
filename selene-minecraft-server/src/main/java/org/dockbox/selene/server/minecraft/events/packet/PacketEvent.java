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

package org.dockbox.selene.server.minecraft.events.packet;

import org.dockbox.selene.api.events.AbstractCancellableEvent;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.minecraft.packets.Packet;
import org.dockbox.selene.server.minecraft.players.Player;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * The event fired when the server <b>sends</b> a packet to a player.
 *
 * @param <T>
 */
public class PacketEvent<T extends Packet> extends AbstractCancellableEvent {

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

    public void setPacket(T packet) {
        this.isModified = true;
        this.packet = packet;
    }

    @Override
    public @NotNull PacketEvent<T> post() {
        Provider.provide(EventBus.class).post(this);
        return this;
    }
}
