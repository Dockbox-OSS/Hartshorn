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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.enums.PortalType;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerPortalEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerSwitchWorldEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerTeleportEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerWarpEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerSpawnEvent;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.dim.SpongeWorld;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent.Reposition;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.world.portal.Portal;

@Posting(value = {
        PlayerSpawnEvent.class,
        PlayerTeleportEvent.class,
        PlayerWarpEvent.class,
        PlayerSwitchWorldEvent.class,
        PlayerPortalEvent.class
})
public class MovementEventBridge implements EventBridge {

    @Listener
    public void on(RespawnPlayerEvent event) {
        final Player player = SpongeAdapter.fromSponge(event.entity());
        final SpongeWorld world = SpongeAdapter.fromSponge(event.destinationWorld());
        this.post(new PlayerSpawnEvent(player, Location.of(world)), event);
    }

    @Listener
    public void on(ChangeEntityWorldEvent event) {
        final Entity entity = event.entity();
        if (entity instanceof ServerPlayer serverPlayer) {
            final Player player = SpongeAdapter.fromSponge(serverPlayer);

            final Exceptional<Portal> portal = Exceptional.of(event.context().get(EventContextKeys.PORTAL));
            portal.present(p -> this.portal(event, player, p));

            final SpongeWorld origin = SpongeAdapter.fromSponge(event.originalWorld());
            final SpongeWorld destination = SpongeAdapter.fromSponge(event.destinationWorld());

            this.post(new PlayerSwitchWorldEvent(player, origin, destination), event);
        }
    }

    private void portal(ChangeEntityWorldEvent event, Player player, Portal portal) {
        final Location origin = SpongeAdapter.fromSponge(portal.origin());
        final Location destination = Exceptional.of(portal.destination()).map(SpongeAdapter::fromSponge).or(Location.empty());
        final PortalType portalType = SpongeAdapter.fromSponge(portal.type());

        this.post(new PlayerPortalEvent(player, origin, destination, true, portalType), event);
    }

    @Listener
    public void on(Reposition event) {
        final Entity entity = event.entity();
        if (entity instanceof ServerPlayer serverPlayer) {
            final Player player = SpongeAdapter.fromSponge(serverPlayer);

            final SpongeWorld originWorld = SpongeAdapter.fromSponge(event.originalWorld());
            final Vector3N originPosition = SpongeAdapter.fromSponge(event.originalPosition());
            final SpongeWorld destinationWorld = SpongeAdapter.fromSponge(event.destinationWorld());
            final Vector3N destinationPosition = SpongeAdapter.fromSponge(event.destinationPosition());

            this.post(new PlayerTeleportEvent(player,
                    Location.of(originPosition, originWorld),
                    Location.of(destinationPosition, destinationWorld)
            ), event);
        }
    }

    /**
     * Placeholder for PlayerWarpEvent
     *
     * @param event
     *         The event
     */
    public void on(Void event) {

    }

}
