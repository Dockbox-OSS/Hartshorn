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

import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.api.events.parents.Cancellable;
import org.dockbox.hartshorn.server.minecraft.events.entity.SpawnSource;
import org.dockbox.hartshorn.server.minecraft.events.entity.SummonEntityEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerSummonEntityEvent;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

// TODO: Player summon entity event
@Posting({SummonEntityEvent.class, PlayerSummonEntityEvent.class})
public class EntityEventBridge implements EventBridge {

    @Listener
    public void on(SpawnEntityEvent.Pre event) {
        SpawnType spawnType = event.context().get(EventContextKeys.SPAWN_TYPE)
                .orElseGet(SpawnTypes.CUSTOM);

        final SpawnSource source = SpongeConvert.fromSponge(spawnType);
        for (Entity entity : event.entities()) {
            final org.dockbox.hartshorn.server.minecraft.entities.Entity target = SpongeConvert.fromSponge(entity);
            final Cancellable cancellable = new SummonEntityEvent(target, source).post();
            
            if (cancellable.isCancelled()) {
                event.setCancelled(true);
                // Don't continue iterating if we already cancelled the source event
                return;
            }
        }
    }

}
