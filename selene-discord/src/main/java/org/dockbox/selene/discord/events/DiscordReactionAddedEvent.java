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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public class DiscordReactionAddedEvent extends DiscordMessageAuthorEvent {

    private final MessageReaction reaction;

    /**
     * The event fired when a reaction is added to a message.
     *
     * @param author
     *         The author that added the reaction
     * @param message
     *         The message the reaction was added to
     * @param reaction
     *         The reaction which was added
     */
    public DiscordReactionAddedEvent(User author, Message message, MessageReaction reaction) {
        super(author, message);
        this.reaction = reaction;
    }

    /**
     * Gets the ID of the emote which is represented by the {@link MessageReaction}.
     *
     * @return The ID of the emote (can usually be parsed to a {@link Long}
     */
    public String getEmoteId() {
        return this.getReaction().getReactionEmote().getId();
    }

    public MessageReaction getReaction() {
        return this.reaction;
    }

    /**
     * Gets the name of the emote which is represented by the {@link MessageReaction}.
     *
     * @return The name of the emote
     */
    public String getEmoteName() {
        return this.getReaction().getReactionEmote().getName();
    }
}
