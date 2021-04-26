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

package org.dockbox.selene.discord;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.annotations.Binds;
import org.dockbox.selene.discord.templates.MessageTemplate;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Binds(DiscordPagination.class)
public class SimpleDiscordPagination implements DiscordPagination {

    private final List<Object> pages = SeleneUtils.emptyList();

    @Override
    public void sendTo(MessageChannel channel) {
        Provider.provide(DiscordUtils.class).sendToTextChannel(this, channel);
    }

    @Override
    public void sendTo(User user) {
        Provider.provide(DiscordUtils.class).sendToUser(this, user);
    }

    @Override
    public DiscordPagination addPage(Message... messages) {
        this.pages.addAll(Arrays.asList(messages));
        return this;
    }

    @Override
    public DiscordPagination addPage(MessageEmbed... embed) {
        this.pages.addAll(Arrays.asList(embed));
        return this;
    }

    @Override
    public DiscordPagination addPage(String... messages) {
        this.pages.addAll(
                Arrays.stream(messages)
                        .map(message -> new MessageBuilder().setContent(message).build())
                        .collect(Collectors.toList()));
        return this;
    }

    @Override
    public DiscordPagination addPage(Text... messages) {
        this.pages.addAll(
                Arrays.stream(messages)
                        .map(message -> new MessageBuilder().setContent(message.toStringValue()).build())
                        .collect(Collectors.toList()));
        return this;
    }

    @Override
    public DiscordPagination addPage(MessageTemplate... templates) {
        for (MessageTemplate template : templates) {
            this.addPage(template.getJDAMessage());
        }
        return this;
    }

    @Override
    public Collection<Object> getPages() {
        return SeleneUtils.asUnmodifiableCollection(this.pages);
    }
}
