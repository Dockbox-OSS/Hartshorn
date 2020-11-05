/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.listeners;

import com.google.inject.Inject;

import org.dockbox.selene.core.events.world.WorldEvent.Creating;
import org.dockbox.selene.core.events.world.WorldEvent.Load;
import org.dockbox.selene.core.events.world.WorldEvent.Save;
import org.dockbox.selene.core.events.world.WorldEvent.Unload;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ConstructWorldPropertiesEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.world.storage.WorldProperties;

public class SpongeWorldListener {

    @Inject
    private EventBus bus;

    @Listener
    public void onWorldLoaded(LoadWorldEvent loadEvent) {
        World world = SpongeConversionUtil.fromSponge(loadEvent.getTargetWorld());
        Event event = new Load(world);
        this.bus.post(event);
    }

    @Listener
    public void onWorldUnloaded(UnloadWorldEvent unloadEvent) {
        World world = SpongeConversionUtil.fromSponge(unloadEvent.getTargetWorld());
        Event event = new Unload(world);
        this.bus.post(event);
    }

    @Listener
    public void onWorldSaved(SaveWorldEvent saveEvent) {
        World world = SpongeConversionUtil.fromSponge(saveEvent.getTargetWorld());
        Event event = new Save(world);
        this.bus.post(event);
    }

    @Listener
    public void onWorldCreating(ConstructWorldPropertiesEvent constructEvent) {
        WorldProperties swp = constructEvent.getWorldProperties();
        Event event = new Creating(SpongeConversionUtil.fromSponge(swp));
        this.bus.post(event);
    }

}
