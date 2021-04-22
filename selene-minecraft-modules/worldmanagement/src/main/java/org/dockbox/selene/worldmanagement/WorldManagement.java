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

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.minecraft.dimension.Worlds;
import org.dockbox.selene.minecraft.dimension.position.Location;
import org.dockbox.selene.minecraft.dimension.world.World;
import org.dockbox.selene.server.events.ServerReloadEvent;
import org.dockbox.selene.server.events.ServerStartedEvent;
import org.dockbox.selene.server.minecraft.events.player.PlayerPortalEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@Command(aliases = {"unloader", "wu"}, usage = "unloader", permission = WorldManagementResources.WORLD_MANAGER)
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

    @Command(aliases = "blacklist", usage = "blacklist <world{String}>", permission = WorldManagementResources.WORLD_MANAGER)
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
