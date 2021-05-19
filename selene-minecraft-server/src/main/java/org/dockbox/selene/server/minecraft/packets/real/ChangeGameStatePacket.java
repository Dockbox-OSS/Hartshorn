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

package org.dockbox.selene.server.minecraft.packets.real;

import org.dockbox.selene.di.annotations.RequiresBinding;
import org.dockbox.selene.server.minecraft.packets.Packet;
import org.dockbox.selene.server.minecraft.packets.data.Weather;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@RequiresBinding
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@Data
public abstract class ChangeGameStatePacket extends Packet {

    private Weather weather = Weather.CLEAR;

    @Override
    protected Class<?> internalGetPacketType() throws ClassNotFoundException {
        return Class.forName("net.minecraft.network.play.server.SPacketChangeGameState");
    }
}
