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

package org.dockbox.selene.common.discord;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.api.discord.DiscordPagination;
import org.dockbox.selene.api.discord.DiscordUtils;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Collection;
import java.util.List;

public class SimpleDiscordPagination implements DiscordPagination
{

    private final List<Object> pages = SeleneUtils.emptyList();

    @Override
    public void sendTo(MessageChannel channel)
    {
        Selene.provide(DiscordUtils.class).sendToTextChannel(this, channel);
    }

    @Override
    public void sendTo(User user)
    {
        Selene.provide(DiscordUtils.class).sendToUser(this, user);
    }

    @Override
    public DiscordPagination addPage(Message message)
    {
        this.pages.add(message);
        return this;
    }

    @Override
    public DiscordPagination addPage(MessageEmbed embed)
    {
        this.pages.add(embed);
        return this;
    }

    @Override
    public DiscordPagination addPage(String message)
    {
        return this.addPage(new MessageBuilder().setContent(message).build());
    }

    @Override
    public DiscordPagination addPage(Text message)
    {
        return this.addPage(new MessageBuilder().setContent(message.toStringValue()).build());
    }

    @Override
    public DiscordPagination addPages(Message... messages)
    {
        for (Message message : messages)
            this.addPage(message);
        return this;
    }

    @Override
    public DiscordPagination addPages(MessageEmbed... embeds)
    {
        for (MessageEmbed embed : embeds)
            this.addPage(embed);
        return this;
    }

    @Override
    public DiscordPagination addPages(String... messages)
    {
        for (String message : messages)
            this.addPage(message);
        return this;
    }

    @Override
    public DiscordPagination addPages(Text... messages)
    {
        for (Text message : messages)
            this.addPage(message);
        return this;
    }

    @Override
    public Collection<Object> getPages()
    {
        return SeleneUtils.asUnmodifiableCollection(this.pages);
    }
}
