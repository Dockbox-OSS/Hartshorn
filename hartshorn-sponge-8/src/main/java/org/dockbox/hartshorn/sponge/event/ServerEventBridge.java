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

import org.dockbox.hartshorn.server.minecraft.events.server.ServerInitEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerPostInitEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerReloadEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStartedEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStartingEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStoppingEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerUpdateEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;

public class ServerEventBridge implements EventBridge {

    @Listener
    public void on(StartingEngineEvent<?> event) {
        new ServerInitEvent().post();
    }

    @Listener
    public void on(StartedEngineEvent<?> event) {
        new ServerPostInitEvent().post();
        new ServerStartingEvent().post();
    }

    @Listener
    public void on(LoadedGameEvent event) {
        new ServerStartedEvent().post();
        new ServerUpdateEvent().post();
    }

    @Listener
    public void on(StoppingEngineEvent<?> event) {
        new ServerStoppingEvent().post();
    }

    @Listener
    public void on(RefreshGameEvent event) {
        new ServerReloadEvent().post();
        new ServerUpdateEvent().post();
    }
}
