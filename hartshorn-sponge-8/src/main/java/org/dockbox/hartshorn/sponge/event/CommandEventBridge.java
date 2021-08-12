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

package org.dockbox.hartshorn.sponge.event;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.events.NativeCommandEvent;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.ExecuteCommandEvent;

@Posting(NativeCommandEvent.class)
public class CommandEventBridge implements EventBridge {

    @Listener
    public void on(ExecuteCommandEvent.Pre event) {
        CommandSource source = SpongeConvert.fromSponge(event.commandCause().subject()).orNull();
        this.post(new NativeCommandEvent(
                source,
                event.originalCommand(),
                event.originalArguments().split(" ")
        ), event);
    }

}
