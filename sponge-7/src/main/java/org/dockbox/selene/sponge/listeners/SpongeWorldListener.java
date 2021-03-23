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

import org.dockbox.selene.api.events.parents.Cancellable;
import org.dockbox.selene.api.events.world.WorldCreatingEvent;
import org.dockbox.selene.api.events.world.WorldLoadEvent;
import org.dockbox.selene.api.events.world.WorldSaveEvent;
import org.dockbox.selene.api.events.world.WorldUnloadEvent;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ConstructWorldPropertiesEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.world.storage.WorldProperties;

public class SpongeWorldListener {

    @Listener
    public void onWorldLoaded(LoadWorldEvent loadEvent) {
        World world = SpongeConversionUtil.fromSponge(loadEvent.getTargetWorld());
        Cancellable event = new WorldLoadEvent(world).post();
        loadEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onWorldUnloaded(UnloadWorldEvent unloadEvent) {
        Cancellable event = new WorldUnloadEvent(unloadEvent.getTargetWorld().getUniqueId()).post();
        unloadEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onWorldSaved(SaveWorldEvent saveEvent) {
        World world = SpongeConversionUtil.fromSponge(saveEvent.getTargetWorld());
        Cancellable event = new WorldSaveEvent(world).post();
        saveEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onWorldCreating(ConstructWorldPropertiesEvent constructEvent) {
        WorldProperties swp = constructEvent.getWorldProperties();
        new WorldCreatingEvent(SpongeConversionUtil.fromSpongeCreating(swp)).post();
    }
}
