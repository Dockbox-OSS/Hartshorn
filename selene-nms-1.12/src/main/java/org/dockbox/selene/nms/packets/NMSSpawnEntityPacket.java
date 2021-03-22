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

package org.dockbox.selene.nms.packets;

import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;

import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.nms.entities.NMSEntity;
import org.dockbox.selene.packets.SpawnEntityPacket;

public class NMSSpawnEntityPacket<T extends Entity<?>> extends SpawnEntityPacket<T> implements NMSPacket<SPacketSpawnGlobalEntity> {

    @Override
    public SPacketSpawnGlobalEntity getPacket() {
        Entity<?> entity = this.getEntity();
        if (entity instanceof NMSEntity) {
            return new SPacketSpawnGlobalEntity(((NMSEntity<?>) entity).getEntity());
        }
        else {
            throw new UnsupportedOperationException(
                    "Cannot convert entity '" + entity + "' to NMSEntity");
        }
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... injectorProperties) {
        // TODO, Implementation of Entity
    }
}
