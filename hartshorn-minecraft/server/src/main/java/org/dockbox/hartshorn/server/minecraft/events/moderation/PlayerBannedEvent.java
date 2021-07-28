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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Subject;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.time.LocalDateTime;

public class PlayerBannedEvent extends BanEvent<Player> {

    /**
     * The event fired when a {@link Subject} is banned, typically this is a {@link Player}.
     *
     * @param target
     *         The player being banned
     * @param reason
     *         The reason of the ban
     * @param source
     *         The {@link CommandSource} executing the ban
     * @param expiration
     *         The {@link LocalDateTime} of when the ban expires, if present
     * @param creation
     *         The {@link LocalDateTime} of when the ban was issued.
     */
    public PlayerBannedEvent(
            Player target,
            CommandSource source,
            Exceptional<String> reason,
            Exceptional<LocalDateTime> expiration,
            LocalDateTime creation
    ) {
        super(target, source, reason, expiration, creation);
    }
}
