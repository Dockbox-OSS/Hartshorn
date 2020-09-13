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

package org.dockbox.darwin.sponge.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.dockbox.darwin.core.events.discord.DiscordCommandContext;
import org.dockbox.darwin.core.events.discord.DiscordEvent;
import org.dockbox.darwin.core.events.discord.DiscordEvent.ChatDeleted;
import org.dockbox.darwin.core.events.discord.DiscordEvent.PrivateChatDeleted;
import org.dockbox.darwin.core.events.discord.DiscordEvent.PrivateChatReceived;
import org.dockbox.darwin.core.events.discord.DiscordEvent.ReactionAdded;
import org.dockbox.darwin.core.objects.events.Event;
import org.dockbox.darwin.core.util.discord.DiscordUtils;
import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.sponge.SpongeServer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpongeDiscordListener extends ListenerAdapter {

    private final EventBus bus = SpongeServer.getInstance(EventBus.class);

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Event cre = new DiscordEvent.ChatReceived(event.getAuthor(), event.getMessage(), event.getGuild(), event.getChannel());
        this.bus.post(cre);
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        Event pcre = new PrivateChatReceived(event.getAuthor(), event.getMessage());
        this.bus.post(pcre);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String fullCommand = event.getMessage().getContentStripped();
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
            SpongeServer.getInstance(DiscordUtils.class).post(alias, ctx);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        event.getTextChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            User user = event.getJDA().getUserById(event.getUserId());
            if (null != user) {
                Event rae = new ReactionAdded(user, message, event.getReaction());
                this.bus.post(rae);
            }
        });
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        Event cde = new ChatDeleted(event.getMessageId());
        this.bus.post(cde);
    }

    @Override
    public void onPrivateMessageDelete(@NotNull PrivateMessageDeleteEvent event) {
        Event pcde = new PrivateChatDeleted(event.getMessageId());
        this.bus.post(pcde);
    }
}
