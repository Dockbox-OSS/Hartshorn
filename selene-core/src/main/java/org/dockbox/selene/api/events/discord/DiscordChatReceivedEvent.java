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

package org.dockbox.selene.api.events.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordChatReceivedEvent extends DiscordMessageAuthorEvent {

    private final Guild guild;
    private final TextChannel channel;

    /**
     * The event fired when a chat message is received from Discord.
     *
     * @param author
     *         The author of the message
     * @param message
     *         The message
     * @param guild
     *         The Discord guild in which the message was received
     * @param channel
     *         The Discord channel in which the message was received
     */
    public DiscordChatReceivedEvent(User author, Message message, Guild guild, TextChannel channel) {
        super(author, message);
        this.guild = guild;
        this.channel = channel;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public TextChannel getChannel() {
        return this.channel;
    }
}
