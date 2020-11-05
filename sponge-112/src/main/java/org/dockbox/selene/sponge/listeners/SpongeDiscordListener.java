/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.listeners;

import com.google.inject.Inject;

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

import org.dockbox.selene.core.events.discord.DiscordCommandContext;
import org.dockbox.selene.core.events.discord.DiscordEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordUserBannedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordChatDeletedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordChatUpdatedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordBotDisconnectedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordUserJoinedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordUserLeftEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordUserNicknameChangedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordPrivateChatDeletedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordPrivateChatReceivedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordPrivateChatUpdatedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordReactionAddedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordBotReconnectedEvent;
import org.dockbox.selene.core.events.discord.DiscordEvent.DiscordUserUnbannedEvent;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.dockbox.selene.core.util.events.EventBus;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpongeDiscordListener extends ListenerAdapter {

    @Inject
    private EventBus bus;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Event cre = new DiscordEvent.DiscordChatReceivedEvent(event.getAuthor(), event.getMessage(), event.getGuild(), event.getChannel());
        this.bus.post(cre);
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        Event pcre = new DiscordPrivateChatReceivedEvent(event.getAuthor(), event.getMessage());
        this.bus.post(pcre);
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
            List<String> arguments = new ArrayList<>(Arrays.asList(parts));
            arguments.remove(0); // Remove command

            DiscordCommandContext ctx = new DiscordCommandContext(
                    event.getAuthor(),
                    event.getChannel(),
                    LocalDateTime.now(),
                    alias,
                    arguments.toArray(new String[0])
            );
            Selene.getInstance(DiscordUtils.class).post(alias, ctx);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        event.getTextChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            User user = event.getJDA().getUserById(event.getUserId());
            if (null != user) {
                Event rae = new DiscordReactionAddedEvent(user, message, event.getReaction());
                this.bus.post(rae);
            }
        });
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        Event cde = new DiscordChatDeletedEvent(event.getMessageId());
        this.bus.post(cde);
    }

    @Override
    public void onPrivateMessageDelete(@NotNull PrivateMessageDeleteEvent event) {
        Event pcde = new DiscordPrivateChatDeletedEvent(event.getMessageId());
        this.bus.post(pcde);
    }

    @Override
    public void onReconnect(@NotNull ReconnectedEvent event) {
        this.bus.post(new DiscordBotReconnectedEvent());
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        this.bus.post(new DiscordBotDisconnectedEvent());
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        Event cue = new DiscordChatUpdatedEvent(event.getAuthor(), event.getMessage());
        this.bus.post(cue);
    }

    @Override
    public void onPrivateMessageUpdate(@NotNull PrivateMessageUpdateEvent event) {
        Event pcue = new DiscordPrivateChatUpdatedEvent(event.getAuthor(), event.getMessage());
        this.bus.post(pcue);
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        Event be = new DiscordUserBannedEvent(event.getUser(), event.getGuild());
        this.bus.post(be);
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        Event ue = new DiscordUserUnbannedEvent(event.getUser(), event.getGuild());
        this.bus.post(ue);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Event je = new DiscordUserJoinedEvent(event.getUser(), event.getGuild());
        this.bus.post(je);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Event le = new DiscordUserLeftEvent(event.getUser(), event.getGuild());
        this.bus.post(le);
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        Event nce = new DiscordUserNicknameChangedEvent(
                event.getUser(),
                Optional.ofNullable(event.getOldNickname()),
                Optional.ofNullable(event.getNewNickname())
        );
        this.bus.post(nce);
    }
}
