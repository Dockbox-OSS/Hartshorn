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

package org.dockbox.hartshorn.nms.packets;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.server.minecraft.packets.PacketReceiver;

import io.netty.channel.Channel;

/**
 * Represents a native {@link Packet} instance. This is used primarily as a wrapper type for
 * implementations of {@link org.dockbox.hartshorn.server.minecraft.packets.Packet}.
 *
 * @param <T>
 *         The type of the native packet.
 */
public interface NMSPacket<T extends Packet<? extends INetHandler>> extends InjectableType {

    /**
     * Writes the packet to the given channel. This allows writing native packets to Netty channels
     * without implementations requiring access to NMS.
     *
     * @param channel
     *         The channel to send to, typically bound to a specific {@link
     *         PacketReceiver}.
     */
    default void write(Channel channel) {
        channel.write(this.getPacket());
    }

    T getPacket();
}