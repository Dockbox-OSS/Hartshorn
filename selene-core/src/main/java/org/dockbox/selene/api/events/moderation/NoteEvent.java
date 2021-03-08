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
import org.dockbox.selene.api.objects.player.Player;

public abstract class NoteEvent extends ModerationEvent {

    private String note;

    /**
     * The abstract type which can be used to listen to all player note related events.
     *
     * @param note
     *         The note
     * @param source
     *         The {@link CommandSource} creating the note
     * @param player
     *         The target {@link Player} the note is being added to
     */
    protected NoteEvent(Player player, CommandSource source, String note) {
        super(player, source);
        this.note = note;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static class PlayerNotedEvent extends NoteEvent {

        /**
         * The event fire when a new note is added to a player.
         *
         * @param player
         *         The target {@link Player} the note is being added to
         * @param note
         *         The note
         * @param source
         *         The {@link CommandSource} creating the note
         */
        public PlayerNotedEvent(Player player, CommandSource source, String note) {
            super(player, source, note);
        }
    }
}
