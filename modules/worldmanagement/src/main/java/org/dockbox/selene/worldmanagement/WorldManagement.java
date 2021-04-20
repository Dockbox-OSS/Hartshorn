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

package org.dockbox.selene.worldmanagement;

import com.google.inject.Inject;

import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.events.player.PlayerPortalEvent;
import org.dockbox.selene.api.events.server.ServerReloadEvent;
import org.dockbox.selene.api.events.server.ServerStartedEvent;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.tasks.TaskRunner;

import java.util.concurrent.TimeUnit;

@Command(aliases = {"unloader", "wu"}, usage = "unloader")
@Module(id = "worldmanagement", name = "World Management", description = "Manages several aspects of the various worlds on a server", authors = "GuusLieben")
public class WorldManagement {

    @Inject
    private WorldManagementConfig config;

    @Listener
    public void on(ServerReloadEvent event) {
        this.config = Selene.provide(WorldManagementConfig.class); // Reload from file, clean instance
    }

    @Listener
    public void on(ServerStartedEvent event) {
        Selene.provide(TaskRunner.class).acceptDelayed(this::unloadEmptyWorlds, 5, TimeUnit.MINUTES);
    }

    @Listener
    public void on(PlayerPortalEvent event) {
        if (event.usesPortal() && event.getNewLocation().getWorld().getName().equals(this.config.getPortalWorldTarget())) {
            event.setUsePortal(false);
            event.setNewLocation(new Location(this.config.getPortalPosition(), event.getNewLocation().getWorld()));
        }
    }

    @Command(aliases = "blacklist", usage = "blacklist <world{String}>")
    public void blacklist(CommandSource src, String world) {
        if (Selene.provide(Worlds.class).hasWorld(world)) {
            this.config.getUnloadBlacklist().add(world);
            this.config.save();
            src.send(WorldManagementResources.WORLD_BLACKLIST_ADDED.format(world));
        } else {
            src.send(WorldManagementResources.WORLD_BLACKLIST_FAILED.format(world));
        }
    }

    private void unloadEmptyWorlds() {
        Selene.provide(Worlds.class).getLoadedWorlds()
                .stream()
                .filter(world -> world.getPlayerCount() == 0)
                .filter(world -> this.config.getUnloadBlacklist().contains(world.getName()))
                .limit(this.config.getMaximumWorldsToUnload())
                .forEach(World::unload);
        Selene.provide(TaskRunner.class).acceptDelayed(this::unloadEmptyWorlds, this.config.getUnloadDelay(), TimeUnit.MINUTES);
    }
}
