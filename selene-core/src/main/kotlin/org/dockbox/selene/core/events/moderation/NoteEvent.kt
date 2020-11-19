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

import org.dockbox.selene.core.events.AbstractTargetEvent
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.objects.user.Player

/**
 * The abstract type which can be used to listen to all player note related events.
 *
 * @property note The note
 * @property source The [CommandSource] creating the note
 *
 * @param player The target [Player] the note is being added to
 */
abstract class NoteEvent(player: Player, val note: String, val source: CommandSource) : AbstractTargetEvent(player) {

    /**
     * The event fire when a new note is added to a player.
     *
     * @param player The target [Player] the note is being added to
     * @param note The note
     * @param source The [CommandSource] creating the note
     */
    class PlayerNotedEvent(player: Player, note: String, source: CommandSource) : NoteEvent(player, note, source)

}
