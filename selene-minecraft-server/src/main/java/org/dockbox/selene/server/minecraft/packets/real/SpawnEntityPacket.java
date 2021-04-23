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
import org.dockbox.selene.server.minecraft.entities.Entity;
import org.dockbox.selene.server.minecraft.packets.Packet;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@RequiresBinding
public abstract class SpawnEntityPacket extends Packet {

    private Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected Class<?> internalGetPacketType() throws ClassNotFoundException {
        return Class.forName("net.minecraft.network.play.server.SPacketSpawnGlobalEntity");
    }
}
