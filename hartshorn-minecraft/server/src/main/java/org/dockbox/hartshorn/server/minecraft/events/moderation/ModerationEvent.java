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

package org.dockbox.hartshorn.server.minecraft.events.moderation;

import org.dockbox.hartshorn.events.AbstractTargetEvent;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import lombok.Getter;

public abstract class ModerationEvent extends AbstractTargetEvent {

    @Getter
    private final CommandSource source;

    protected ModerationEvent(Player player, CommandSource source) {
        super(player);
        this.source = source;
    }
}
