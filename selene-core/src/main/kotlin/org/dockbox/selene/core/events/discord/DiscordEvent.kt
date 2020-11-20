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

/**
 * The abstract type which can be used to listen to all Discord related events.
 */
abstract class DiscordEvent : Event {

    /**
     * The event fired when a chat message is received from Discord.
     *
     * @property author The author of the message
     * @property message The message
     * @property guild The Discord guild in which the message was received
     * @property channel The Discord channel in which the message was received
     */
    class DiscordChatReceivedEvent(
            val author: User,
            val message: Message,
            val guild: Guild,
            val channel: TextChannel
    ): DiscordEvent()

    /**
     * The event fired when a private chat message is received from Discord. This only includes messages sent to the
     * bot in direct messages.
     *
     * @property author The author of the message
     * @property message The message
     */
    class DiscordPrivateChatReceivedEvent(
            val author: User,
            val message: Message
    ): DiscordEvent()

    /**
     * The event fired when a reaction is added to a message.
     *
     * @property author The author that added the reaction
     * @property message The message the reaction was added to
     * @property reaction The reaction which was added
     */
    class DiscordReactionAddedEvent(
            val author: User,
            val message: Message,
            val reaction: MessageReaction
    ): DiscordEvent() {
        /**
         * Gets the ID of the emote which is represented by the [MessageReaction].
         *
         * @return
         */
        fun getEmoteId(): String = reaction.reactionEmote.id

        /**
         * Gets the name of the emote which is represented by the [MessageReaction].
         *
         * @return
         */
        fun getEmoteName(): String = reaction.reactionEmote.name
    }

    /**
     * The event fired when a message is deleted.
     *
     * @property messageId The ID of the message.
     */
    class DiscordChatDeletedEvent(
            val messageId: String
    ) : DiscordEvent()

    /**
     * The event fired when a private chat message is deleted.
     *
     * @property messageId The ID of the message
     */
    class DiscordPrivateChatDeletedEvent(
            val messageId: String
    ) : DiscordEvent()

    /**
     * The event fired when a message was updated.
     *
     * @property author The author of the message
     * @property message The new value of the message
     */
    class DiscordChatUpdatedEvent(
            val author: User,
            val message: Message
    ) : DiscordEvent()

    /**
     * The event fired when a private message was updated.
     *
     * @property author The author of the message
     * @property message The new value of the message
     */
    class DiscordPrivateChatUpdatedEvent(
            val author: User,
            val message: Message
    ) : DiscordEvent()

    /**
     * The event fired when a user is banned from a Discord guild.
     *
     * @property user The banned user
     * @property guild The guild the user was banned from
     * @constructor Create empty Discord user banned event
     */
    class DiscordUserBannedEvent(val user: User, val guild: Guild) : DiscordEvent()

    /**
     * The event fired when a user is unbanned/pardonned from a Discord guild.
     *
     * @property user The unbanned user.
     * @property guild The guild the user was unbanned from
     */
    class DiscordUserUnbannedEvent(val user: User, val guild: Guild) : DiscordEvent()

    /**
     * The event fired when a new user joins a Discord guild.
     *
     * @property user The user which joined the guild
     * @property guild The guild the user joined
     */
    class DiscordUserJoinedEvent(val user: User, val guild: Guild) : DiscordEvent()

    /**
     * The event fired when a user leaves a Discord guild.
     *
     * @property user The user which left the guild
     * @property guild The guild the user left
     */
    class DiscordUserLeftEvent(val user: User, val guild: Guild) : DiscordEvent()

    /**
     * The event fired when a user's nickname is changed.
     *
     * @property user The user of which the nickname changed
     * @property oldNickname The previous value of the nickname
     * @property newNickname The new (and current) value of the nickname
     */
    class DiscordUserNicknameChangedEvent(
            val user: User,
            val oldNickname: Optional<String>,
            val newNickname: Optional<String>
    ) : DiscordEvent()

    /**
     * The event fired when the Discord bot disconnects from Discord.
     */
    class DiscordBotDisconnectedEvent : DiscordEvent()

    /**
     * The event fired when the Discord bot (re)connects to Discord.
     */
    class DiscordBotReconnectedEvent : DiscordEvent()
}
