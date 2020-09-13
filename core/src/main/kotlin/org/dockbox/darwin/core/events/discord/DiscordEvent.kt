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

package org.dockbox.darwin.core.events.discord

import net.dv8tion.jda.api.entities.*
import org.dockbox.darwin.core.objects.events.Event

abstract class DiscordEvent : Event {

    class ChatReceived(
            val author: User,
            val message: Message,
            val guild: Guild,
            val channel: TextChannel
    ): DiscordEvent()

    class PrivateChatReceived(
            val author: User,
            val message: Message
    ): DiscordEvent()

    class ReactionAdded(
            val author: User,
            val message: Message,
            val reaction: MessageReaction
    ): DiscordEvent() {
        fun getEmoteId(): String = reaction.reactionEmote.id
        fun getEmoteName(): String = reaction.reactionEmote.name
    }

    class ChatDeleted(
            val messageId: String
    ): DiscordEvent()

    class PrivateChatDeleted(
            val messageId: String
    ): DiscordEvent()

}
