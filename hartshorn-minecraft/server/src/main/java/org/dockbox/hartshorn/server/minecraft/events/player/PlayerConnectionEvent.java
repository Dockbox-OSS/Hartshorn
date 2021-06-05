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

package org.dockbox.hartshorn.server.minecraft.events.player;

import org.dockbox.hartshorn.api.domain.Target;
import org.dockbox.hartshorn.api.events.AbstractTargetEvent;
import org.dockbox.hartshorn.server.minecraft.players.Player;

/** The abstract type which can be used to listen to all player movement related events. */
public abstract class PlayerConnectionEvent extends AbstractTargetEvent {

    protected PlayerConnectionEvent(Player target) {
        super(target);
    }

    @Override
    public void setTarget(Target target) {
        throw new UnsupportedOperationException("Cannot change target of connection event");
    }

    @Override
    public Player getTarget() {
        return (Player) super.getTarget();
    }
}