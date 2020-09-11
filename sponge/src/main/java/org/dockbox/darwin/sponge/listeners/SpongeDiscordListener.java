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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.dockbox.darwin.core.events.discord.DiscordEvent;
import org.dockbox.darwin.core.events.discord.DiscordEvent.PrivateChatReceived;
import org.dockbox.darwin.core.objects.events.Event;
import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.sponge.SpongeServer;
import org.jetbrains.annotations.NotNull;

public class SpongeDiscordListener extends ListenerAdapter {

    private final EventBus bus = SpongeServer.getInstance(EventBus.class);

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Event dec = new DiscordEvent.Chat(event.getAuthor(), event.getMessage(), event.getGuild(), event.getChannel());
        this.bus.post(dec);
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        Event depcr = new PrivateChatReceived(event.getAuthor(), event.getMessage());
        this.bus.post(depcr);
    }
}
