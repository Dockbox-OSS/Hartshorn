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

import org.dockbox.selene.server.events.ServerPostInitEvent;
import org.dockbox.selene.server.events.ServerReloadEvent;
import org.dockbox.selene.server.events.ServerStartedEvent;
import org.dockbox.selene.server.events.ServerStartingEvent;
import org.dockbox.selene.server.events.ServerStoppingEvent;
import org.dockbox.selene.server.events.ServerUpdateEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;

public class SpongeServerListener {

    @Listener
    public void on(GameStartingServerEvent event) {
        new ServerStartingEvent().post();
    }

    @Listener
    public void on(GameStartedServerEvent event) {
        new ServerStartedEvent().post();
        new ServerUpdateEvent().post();
    }

    @Listener
    public void on(GameReloadEvent event) {
        new ServerReloadEvent().post();
        new ServerUpdateEvent().post();
    }

    @Listener
    public void on(GameStoppingServerEvent event) {
        new ServerStoppingEvent().post();
    }

    @Listener
    public void on(GameLoadCompleteEvent event) {
        new ServerPostInitEvent().post();
    }
}
