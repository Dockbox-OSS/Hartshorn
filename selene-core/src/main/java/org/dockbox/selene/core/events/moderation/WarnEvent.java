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

package org.dockbox.selene.core.events.moderation;

import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.objects.player.Player;

import java.time.LocalDateTime;

public abstract class WarnEvent extends ModerationEvent
{

    private String reason;

    protected WarnEvent(Player player, CommandSource source, String reason)
    {
        super(player, source);
        this.reason = reason;
    }

    public String getReason()
    {
        return this.reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public static class PlayerWarnedEvent extends WarnEvent
    {

        private final LocalDateTime created;

        /**
         * The event fired when a player is warned
         *
         * @param created
         *         The time at which the warning was originally issued
         * @param player
         *         The target player being warned
         * @param reason
         *         The reason of the warning
         * @param source
         *         The {@link CommandSource} executing the warning
         */
        public PlayerWarnedEvent(Player player, CommandSource source, String reason, LocalDateTime created)
        {
            super(player, source, reason);
            this.created = created;
        }

        public LocalDateTime getCreated()
        {
            return this.created;
        }
    }

    public static class PlayerWarningExpired extends WarnEvent
    {

        /**
         * The event fired when a warning expires. This can be either a automatic expiration based on a time constraint, or
         * it being deleted by another {@link CommandSource}.
         *
         * @param player
         *         The target player being warned
         * @param reason
         *         The reason of the warning
         * @param source
         *         The {@link CommandSource} executing the warning
         */
        public PlayerWarningExpired(Player player, CommandSource source, String reason)
        {
            super(player, source, reason);
        }
    }
}
