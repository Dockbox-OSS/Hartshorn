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

package org.dockbox.selene.discord.events;

import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.api.domain.Exceptional;

public class DiscordUserNicknameChangedEvent extends DiscordEvent {

    private final User user;
    private final Exceptional<String> oldNickname;
    private final Exceptional<String> newNickname;

    /**
     * The event fired when a user's nickname is changed.
     *
     * @param user
     *         The user of which the nickname changed
     * @param oldNickname
     *         The previous value of the nickname
     * @param newNickname
     *         The new (and current) value of the nickname
     */
    public DiscordUserNicknameChangedEvent(User user, Exceptional<String> oldNickname, Exceptional<String> newNickname) {
        this.user = user;
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
    }

    public User getUser() {
        return this.user;
    }

    public Exceptional<String> getOldNickname() {
        return this.oldNickname;
    }

    public Exceptional<String> getNewNickname() {
        return this.newNickname;
    }
}
