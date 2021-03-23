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

package org.dockbox.selene.api.events.player.interact;

import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.objects.player.ClickType;
import org.dockbox.selene.api.objects.player.Hand;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.tuple.Vector3N;

public final class PlayerInteractEntityEvent<T extends Entity<?>> extends PlayerInteractEvent {

    private final T entity;
    private final Vector3N interactionPoint;

    public PlayerInteractEntityEvent(Player player, T entity, Vector3N interactionPoint) {
        super(player, Hand.EITHER, ClickType.SECONDARY);
        this.entity = entity;
        this.interactionPoint = interactionPoint;
    }

    public Vector3N getInteractionPoint() {
        return this.interactionPoint;
    }

    public T getEntity() {
        return this.entity;
    }
}
