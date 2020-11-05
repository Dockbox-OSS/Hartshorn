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

package org.dockbox.selene.core.events.discord

import java.util.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.dockbox.selene.core.objects.events.Event

abstract class DiscordEvent : Event {

    class DiscordChatReceivedEvent(
            val author: User,
            val message: Message,
            val guild: Guild,
            val channel: TextChannel
    ): DiscordEvent()

    class DiscordPrivateChatReceivedEvent(
            val author: User,
            val message: Message
    ): DiscordEvent()

    class DiscordReactionAddedEvent(
            val author: User,
            val message: Message,
            val reaction: MessageReaction
    ): DiscordEvent() {
        fun getEmoteId(): String = reaction.reactionEmote.id
        fun getEmoteName(): String = reaction.reactionEmote.name
    }

    class DiscordChatDeletedEvent(
            val messageId: String
    ) : DiscordEvent()

    class DiscordPrivateChatDeletedEvent(
            val messageId: String
    ) : DiscordEvent()

    class DiscordChatUpdatedEvent(
            val author: User,
            val message: Message
    ) : DiscordEvent()

    class DiscordPrivateChatUpdatedEvent(
            val author: User,
            val message: Message
    ) : DiscordEvent()

    class DiscordUserBannedEvent(val user: User, val guild: Guild) : DiscordEvent()

    class DiscordUserUnbannedEvent(val user: User, val guild: Guild) : DiscordEvent()

    class DiscordUserJoinedEvent(val user: User, val guild: Guild) : DiscordEvent()

    class DiscordUserLeftEvent(val user: User, val guild: Guild) : DiscordEvent()

    class DiscordUserNicknameChangedEvent(
            val user: User,
            val oldNickname: Optional<String>,
            val newNickname: Optional<String>
    ) : DiscordEvent()

    class DiscordBotDisconnectedEvent : DiscordEvent()

    class DiscordBotReconnectedEvent : DiscordEvent()
}
