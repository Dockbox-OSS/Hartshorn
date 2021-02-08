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

package org.dockbox.selene.api.events.moderation;

import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.player.Player;

public class KickEvent extends ModerationEvent
{

    private final Exceptional<String> reason;

    /**
     * The event fired when a {@link Player} is kicked from the server.
     *
     * @param player
     *         The {@link Player} being kicked
     * @param source
     *         The {@link CommandSource} executing the kick
     * @param reason
     *         The reason, if provided
     */
    public KickEvent(Player player, CommandSource source, Exceptional<String> reason)
    {
        super(player, source);
        this.reason = reason;
    }

    public Exceptional<String> getReason()
    {
        return this.reason;
    }
}
