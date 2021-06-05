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

package org.dockbox.hartshorn.worldedit.events;

import org.dockbox.hartshorn.api.events.AbstractCancellableEvent;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.worldedit.WorldEditKeys;
import org.dockbox.hartshorn.worldedit.region.Region;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Cancellable event which is executed when a player performs a copy action using WorldEdit. Note
 * that this event is fired <i>before</i> the clipboard is populated.
 */
@Getter
@AllArgsConstructor
public class WorldEditCopyEvent extends AbstractCancellableEvent {

    private final Player player;
    /**
     * Gets the selection of the executing {@link Player}
     *
     * @return The selection
     */
    public Exceptional<Region> getSelection() {
        return this.player.get(WorldEditKeys.SELECTION);
    }
}
