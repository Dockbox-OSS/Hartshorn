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

package org.dockbox.hartshorn.server.minecraft.packets.real;

import org.dockbox.hartshorn.di.annotations.Required;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.packets.Packet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@Required
@Data
public abstract class SpawnEntityPacket extends Packet {

    private Entity entity;

    @Override
    protected Class<?> internalGetPacketType() throws ClassNotFoundException {
        return Class.forName("net.minecraft.network.play.server.SPacketSpawnGlobalEntity");
    }
}
