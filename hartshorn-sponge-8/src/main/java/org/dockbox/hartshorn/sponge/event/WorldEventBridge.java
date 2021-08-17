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

import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.events.world.WorldCreatingEvent;
import org.dockbox.hartshorn.server.minecraft.events.world.WorldLoadEvent;
import org.dockbox.hartshorn.server.minecraft.events.world.WorldSaveEvent;
import org.dockbox.hartshorn.server.minecraft.events.world.WorldUnloadEvent;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;

@Posting({ WorldSaveEvent.class, WorldUnloadEvent.class, WorldLoadEvent.class, WorldCreatingEvent.class })
public class WorldEventBridge implements EventBridge {

    @Listener
    public void on(SaveWorldEvent.Pre event) {
        final World world = SpongeAdapter.fromSponge(event.world());
        this.post(new WorldSaveEvent(world), event);
    }

    @Listener
    public void on(UnloadWorldEvent event) {
        this.post(new WorldUnloadEvent(event.world().uniqueId()), event);
    }

    @Listener
    public void on(LoadWorldEvent event) {
        final World world = SpongeAdapter.fromSponge(event.world());
        this.post(new WorldLoadEvent(world), event);
    }

    /**
     * Placeholder for WorldCreating / WorldTemplateSaving events in Sponge. These are
     * currently not in the API and not implemented. However discussion for this has
     * taken place and support is scheduled. See <a href="https://discord.com/channels/142425412096491520/142425521391665153/862698918492241930">this conversation</a>
     * for more details.
     *
     * @param event
     *         The event placeholder
     */
    public void on(Void event) {
        throw new NotImplementedException();
    }

}
