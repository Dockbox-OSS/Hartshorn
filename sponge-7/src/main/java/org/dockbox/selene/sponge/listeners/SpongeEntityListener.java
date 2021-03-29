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

import org.dockbox.selene.api.events.entity.SpawnSource;
import org.dockbox.selene.api.events.entity.SummonEntityEvent;
import org.dockbox.selene.api.events.parents.Cancellable;
import org.dockbox.selene.api.events.player.interact.PlayerSummonEntityEvent;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SpongeEntityListener {

    @Listener
    public void onEntitySpawnByPlayer(SpawnEntityEvent event, @First Player player) {
        SpawnType spawnType = event.getCause().getContext().get(EventContextKeys.SPAWN_TYPE).orElse(null);
        if (SpawnTypes.PLACEMENT.equals(spawnType) || SpawnTypes.SPAWN_EGG.equals(spawnType)) {
            SpawnSource source = SpongeConversionUtil.fromSponge(spawnType);
            org.dockbox.selene.api.objects.player.Player selenePlayer = SpongeConversionUtil.fromSponge(player);

            for (Entity entity : event.getEntities()) {
                Cancellable summonEntityEvent = new PlayerSummonEntityEvent<>(
                        SpongeConversionUtil.fromSponge(entity),
                        source,
                        selenePlayer
                );
                summonEntityEvent.post();
                event.setCancelled(summonEntityEvent.isCancelled());
            }
        }
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        SpawnType spawnType = event.getCause().getContext().get(EventContextKeys.SPAWN_TYPE).orElse(null);
        SpawnSource source = SpongeConversionUtil.fromSponge(spawnType);

        for (Entity entity : event.getEntities()) {
            Cancellable summonEntityEvent = new SummonEntityEvent<>(
                    SpongeConversionUtil.fromSponge(entity),
                    source
            );
            summonEntityEvent.post();
            event.setCancelled(summonEntityEvent.isCancelled());
        }
    }

}
