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

package org.dockbox.hartshorn.sample;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.events.annotations.Listener;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.server.minecraft.events.packet.PacketEvent;
import org.dockbox.hartshorn.server.minecraft.packets.annotations.Packet;
import org.dockbox.hartshorn.server.minecraft.packets.real.ChangeGameStatePacket;

@Service
public final class SampleService {

    private SampleService() {}

    @Listener
    @Packet(ChangeGameStatePacket.class)
    public static void onGameStatePacket(PacketEvent<ChangeGameStatePacket> packetEvent) {
        Hartshorn.log()
                .info(
                        "Sending a packet event to "
                                + packetEvent.target().name()
                                + " (GameStateChange: "
                                + packetEvent.packet().weather()
                                + ')');
    }

    // Uses the Custom Parameter from the Cuboid class, with a nested Shape parameter
    @Command(value = "demo", arguments = "<cuboid{Cuboid}>")
    public void buildCuboid(Cuboid cuboid) {
        Hartshorn.log().info("Cuboid: " + cuboid);
    }

}
