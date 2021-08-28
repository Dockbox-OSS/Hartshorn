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

package org.dockbox.hartshorn.worldmanagement;

import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerPortalEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.EngineChangedState;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Reload;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Started;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@Command(value = { "unloader", "wu" }, permission = WorldManagement.WORLD_MANAGER)
public class WorldManagement {

    protected static final String WORLD_MANAGER = "hartshorn.worlds";
    
    @Inject
    private WorldManagementConfig config;

    @Listener
    public void reload(final EngineChangedState<Reload> event) {
        this.config = event.applicationContext().get(WorldManagementConfig.class); // Reload from file, clean instance
    }

    @Listener
    public void started(final EngineChangedState<Started> event) {
        event.applicationContext().get(TaskRunner.class).acceptDelayed(() -> this.unloadEmptyWorlds(event.applicationContext()), 5, TimeUnit.MINUTES);
    }

    @Listener
    public void on(final PlayerPortalEvent event) {
        if (event.usesPortal() && event.destination().world().name().equals(this.config.worldTarget())) {
            event.usesPortal(false);
            event.destination(Location.of(event.applicationContext(), this.config.portalPosition(), event.destination().world()));
        }
    }

    private void unloadEmptyWorlds(final ApplicationContext context) {
        context.get(Worlds.class).loadedWorlds()
                .stream()
                .filter(world -> world.playerCount() == 0)
                .filter(world -> this.config.unloadBlacklist().contains(world.name()))
                .limit(this.config.unloadLimit())
                .forEach(World::unload);
        context.get(TaskRunner.class).acceptDelayed(() -> this.unloadEmptyWorlds(context), this.config.unloadDelay(), TimeUnit.MINUTES);
    }
}
