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
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.api.objects.Exceptional;

/** The abstract type which can be used to listen to all Discord related events. */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class DiscordEvent implements Event {

    @SuppressWarnings("AbstractClassWithoutAbstractMethods")
    public abstract static class DiscordMessageAuthorEvent extends DiscordEvent {

        private final User author;
        private final Message message;

        protected DiscordMessageAuthorEvent(User author, Message message) {
            this.author = author;
            this.message = message;
        }

        public User getAuthor() {
            return this.author;
        }

        public Message getMessage() {
            return this.message;
        }
    }

    @SuppressWarnings("AbstractClassWithoutAbstractMethods")
    public abstract static class DiscordGuildUserEvent extends DiscordEvent {

        private final User user;
        private final Guild guild;

        protected DiscordGuildUserEvent(User user, Guild guild) {
            this.user = user;
            this.guild = guild;
        }

        public User getUser() {
            return this.user;
        }

        public Guild getGuild() {
            return this.guild;
        }
    }

    public static class DiscordChatReceivedEvent extends DiscordMessageAuthorEvent {

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
        public DiscordChatReceivedEvent(
                User author, Message message, Guild guild, TextChannel channel) {
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

    public static class DiscordPrivateChatReceivedEvent extends DiscordMessageAuthorEvent {

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

    public static class DiscordReactionAddedEvent extends DiscordMessageAuthorEvent {

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

    public static class DiscordChatDeletedEvent extends DiscordEvent {

        private final String messageId;

        /**
         * The event fired when a message is deleted.
         *
         * @param messageId
         *         The ID of the message.
         */
        public DiscordChatDeletedEvent(String messageId) {
            this.messageId = messageId;
        }

        public String getMessageId() {
            return this.messageId;
        }
    }

    public static class DiscordPrivateChatDeletedEvent extends DiscordEvent {

        private final String messageId;

        /**
         * The event fired when a private chat message is deleted.
         *
         * @param messageId
         *         The ID of the message.
         */
        public DiscordPrivateChatDeletedEvent(String messageId) {
            this.messageId = messageId;
        }

        public String getMessageId() {
            return this.messageId;
        }
    }

    public static class DiscordChatUpdatedEvent extends DiscordMessageAuthorEvent {

        /**
         * The event fired when a message was updated.
         *
         * @param author
         *         The author of the message
         * @param message
         *         The new value of the message
         */
        public DiscordChatUpdatedEvent(User author, Message message) {
            super(author, message);
        }
    }

    public static class DiscordPrivateChatUpdatedEvent extends DiscordMessageAuthorEvent {

        /**
         * The event fired when a private message was updated.
         *
         * @param author
         *         The author of the message
         * @param message
         *         The new value of the message
         */
        public DiscordPrivateChatUpdatedEvent(User author, Message message) {
            super(author, message);
        }
    }

    public static class DiscordUserBannedEvent extends DiscordGuildUserEvent {

        /**
         * The event fired when a user is banned from a Discord guild.
         *
         * @param user
         *         The banned user
         * @param guild
         *         The guild the user was banned from
         */
        public DiscordUserBannedEvent(User user, Guild guild) {
            super(user, guild);
        }
    }

    public static class DiscordUserUnbannedEvent extends DiscordGuildUserEvent {

        /**
         * The event fired when a user is unbanned/pardonned from a Discord guild.
         *
         * @param user
         *         The unbanned user.
         * @param guild
         *         The guild the user was unbanned from
         */
        public DiscordUserUnbannedEvent(User user, Guild guild) {
            super(user, guild);
        }
    }

    public static class DiscordUserJoinedEvent extends DiscordGuildUserEvent {

        /**
         * The event fired when a new user joins a Discord guild.
         *
         * @param user
         *         The user which joined the guild
         * @param guild
         *         The guild the user joined
         */
        public DiscordUserJoinedEvent(User user, Guild guild) {
            super(user, guild);
        }
    }

    public static class DiscordUserLeftEvent extends DiscordGuildUserEvent {

        /**
         * The event fired when a user leaves a Discord guild.
         *
         * @param user
         *         The user which left the guild
         * @param guild
         *         The guild the user left
         */
        public DiscordUserLeftEvent(User user, Guild guild) {
            super(user, guild);
        }
    }

    public static class DiscordUserNicknameChangedEvent extends DiscordEvent {

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
        public DiscordUserNicknameChangedEvent(
                User user, Exceptional<String> oldNickname, Exceptional<String> newNickname) {
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

    /** The event fired when the Discord bot disconnects from Discord. */
    public static class DiscordBotDisconnectedEvent extends DiscordEvent {
    }

    /** The event fired when the Discord bot (re)connects to Discord. */
    public static class DiscordBotReconnectedEvent extends DiscordEvent {
    }
}
