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

package org.dockbox.selene.nms.properties;

import net.minecraft.network.Packet;

import org.dockbox.selene.di.properties.InjectorProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Provides a native {@link Packet} to a property holder, typically this is only used for {@link
 * org.dockbox.selene.nms.packets.NMSPacket NMSPackets}.
 *
 * @param <T>
 *         The type of the native packet.
 */
@AllArgsConstructor
public class NativePacketProperty<T extends Packet<?>> implements InjectorProperty<T> {

    public static final String KEY = "SeleneSpongeNativePacket";
    @Getter
    private final T object;

    @Override
    public String getKey() {
        return NativePacketProperty.KEY;
    }
}
