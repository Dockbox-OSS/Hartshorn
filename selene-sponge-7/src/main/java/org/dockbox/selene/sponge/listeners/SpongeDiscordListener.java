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

package org.dockbox.selene.sponge.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.discord.DiscordCommandContext;
import org.dockbox.selene.discord.DiscordUtils;
import org.dockbox.selene.discord.events.DiscordBotDisconnectedEvent;
import org.dockbox.selene.discord.events.DiscordBotReconnectedEvent;
import org.dockbox.selene.discord.events.DiscordChatDeletedEvent;
import org.dockbox.selene.discord.events.DiscordChatReceivedEvent;
import org.dockbox.selene.discord.events.DiscordChatUpdatedEvent;
import org.dockbox.selene.discord.events.DiscordPrivateChatDeletedEvent;
import org.dockbox.selene.discord.events.DiscordPrivateChatReceivedEvent;
import org.dockbox.selene.discord.events.DiscordPrivateChatUpdatedEvent;
import org.dockbox.selene.discord.events.DiscordReactionAddedEvent;
import org.dockbox.selene.discord.events.DiscordUserBannedEvent;
import org.dockbox.selene.discord.events.DiscordUserJoinedEvent;
import org.dockbox.selene.discord.events.DiscordUserLeftEvent;
import org.dockbox.selene.discord.events.DiscordUserNicknameChangedEvent;
import org.dockbox.selene.discord.events.DiscordUserUnbannedEvent;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SpongeDiscordListener extends ListenerAdapter {

    @Override
    public void onReconnect(@NotNull ReconnectedEvent event) {
        new DiscordBotReconnectedEvent().post();
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        new DiscordBotDisconnectedEvent().post();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        new DiscordChatReceivedEvent(
                event.getAuthor(),
                event.getMessage(),
                event.getGuild(),
                event.getChannel()
        ).post();
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        new DiscordChatUpdatedEvent(event.getAuthor(), event.getMessage()).post();
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        new DiscordChatDeletedEvent(event.getMessageId()).post();
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        new DiscordPrivateChatReceivedEvent(event.getAuthor(), event.getMessage()).post();
    }

    @Override
    public void onPrivateMessageUpdate(@NotNull PrivateMessageUpdateEvent event) {
        new DiscordPrivateChatUpdatedEvent(event.getAuthor(), event.getMessage()).post();
    }

    @Override
    public void onPrivateMessageDelete(@NotNull PrivateMessageDeleteEvent event) {
        new DiscordPrivateChatDeletedEvent(event.getMessageId()).post();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String fullCommand = event.getMessage().getContentStripped();
        if (fullCommand.isEmpty()) return;

        char prefix = fullCommand.charAt(0);
        if ('*' == prefix) {
            String[] parts = fullCommand.split(" ");
            String alias = parts[0];
            alias = alias.replaceFirst("\\*", ""); // Remove prefix

            // Wrapped in ArrayList as Arrays.asList is immutable by default
            List<String> arguments = SeleneUtils.asList(Arrays.asList(parts));
            arguments.remove(0); // Remove command

            DiscordCommandContext ctx = new DiscordCommandContext(
                    event.getAuthor(),
                    event.getChannel(),
                    LocalDateTime.now(),
                    alias,
                    arguments.toArray(new String[0])
            );
            Provider.provide(DiscordUtils.class).post(alias, ctx);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        event.getTextChannel()
                .retrieveMessageById(event.getMessageId())
                .queue(message -> {
                    User user = event.getUser();
                    if (null != user) {
                        new DiscordReactionAddedEvent(user, message, event.getReaction()).post();
                    }
                });
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        new DiscordUserBannedEvent(event.getUser(), event.getGuild()).post();
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        new DiscordUserUnbannedEvent(event.getUser(), event.getGuild()).post();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        new DiscordUserLeftEvent(event.getUser(), event.getGuild()).post();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        new DiscordUserJoinedEvent(event.getUser(), event.getGuild()).post();
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        Event nce = new DiscordUserNicknameChangedEvent(
                event.getUser(),
                Exceptional.of(event.getOldNickname()),
                Exceptional.of(event.getNewNickname())
        ).post();
    }
}
