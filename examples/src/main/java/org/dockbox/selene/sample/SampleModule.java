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

package org.dockbox.selene.sample;

import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.event.filter.Packet;
import org.dockbox.selene.core.annotations.module.Module;
import org.dockbox.selene.core.events.packet.PacketEvent;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.packets.ChangeGameStatePacket;

@Module(id = "sample", name = "Selene Sample Module", description = "A sample module, providing examples on various tasks",
        authors = "GuusLieben")
public class SampleModule
{

    @Listener
    @Packet(ChangeGameStatePacket.class)
    public static void onGameStatePacket(PacketEvent<ChangeGameStatePacket> packetEvent)
    {
        Selene.log().info("Sending a packet event to " + packetEvent.getTarget().getName() + " (GameStateChange: " + packetEvent.getPacket()
                .getWeather() + ')');
    }

}
