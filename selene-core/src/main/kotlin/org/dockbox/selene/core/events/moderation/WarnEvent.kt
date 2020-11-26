/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.events.moderation

import java.time.LocalDateTime
import org.dockbox.selene.core.events.AbstractTargetEvent
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.objects.user.Player

/**
 * The abstract type which can be used to listen to all player warning related events.
 *
 * @property reason The reason of the warning
 * @property source The [CommandSource] executing the warning
 *
 * @param player The target player being warned
 */
abstract class WarnEvent(player: Player, val reason: String, val source: CommandSource) : AbstractTargetEvent(player) {

    /**
     * The event fired when a player is warned
     *
     * @property creation
     * @constructor
     *
     * @param player The target player being warned
     * @param reason The reason of the warning
     * @param source The [CommandSource] executing the warning
     */
    class PlayerWarnedEvent(
            player: Player,
            reason: String,
            source: CommandSource,
            val creation: LocalDateTime
    ) : WarnEvent(player, reason, source)

    /**
     * The event fired when a warning expires. This can be either a automatic expiration based on a time constraint, or
     * it being deleted by another [CommandSource].
     *
     * @constructor
     *
     * @param player The target player being warned
     * @param reason The reason of the warning
     * @param source The [CommandSource] executing the warning
     */
    class PlayerWarningExpired(
            player: Player,
            reason: String,
            source: CommandSource
    ) : WarnEvent(player, reason, source)

}
