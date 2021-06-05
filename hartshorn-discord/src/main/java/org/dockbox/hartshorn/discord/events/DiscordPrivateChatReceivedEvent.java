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

package org.dockbox.hartshorn.discord.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class DiscordPrivateChatReceivedEvent extends DiscordMessageAuthorEvent {

    /**
     * The event fired when a private chat message is received from Discord. This only includes
     * messages sent to the bot in direct messages.
     *
     * @param author
     *         The author of the message
     * @param message
     *         The message
     */
    public DiscordPrivateChatReceivedEvent(User author, Message message) {
        super(author, message);
    }
}