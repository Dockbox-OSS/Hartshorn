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

package org.dockbox.selene.sponge.listeners;

import org.dockbox.selene.api.events.chat.NativeCommandEvent;
import org.dockbox.selene.api.events.parents.Cancellable;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;

public class SpongeCommandListener {

    @Listener
    public void onNativeCommand(SendCommandEvent commandEvent) {
        if (commandEvent.getSource() instanceof CommandSource) {
            String command = commandEvent.getCommand();
            String argsJoined = commandEvent.getArguments();
            String[] args = argsJoined.split(" ");
            Cancellable event =
                    new NativeCommandEvent(
                            SpongeConversionUtil.fromSponge((CommandSource) commandEvent.getSource()).orNull(),
                            command,
                            args);
            commandEvent.setCancelled(event.post().isCancelled());
        }
    }
}
