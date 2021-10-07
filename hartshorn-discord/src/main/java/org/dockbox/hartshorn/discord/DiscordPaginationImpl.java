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

package org.dockbox.hartshorn.discord;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.discord.templates.MessageTemplate;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

@Binds(DiscordPagination.class)
public class DiscordPaginationImpl implements DiscordPagination {

    private final List<Object> pages = HartshornUtils.emptyList();
    @Inject
    private ApplicationContext context;

    @Override
    public void send(final MessageChannel channel) {
        this.context.get(DiscordUtils.class).send(this, channel);
    }

    @Override
    public void send(final User user) {
        this.context.get(DiscordUtils.class).send(this, user);
    }

    @Override
    public DiscordPagination add(final Message... messages) {
        this.pages.addAll(Arrays.asList(messages));
        return this;
    }

    @Override
    public DiscordPagination add(final MessageEmbed... embed) {
        this.pages.addAll(Arrays.asList(embed));
        return this;
    }

    @Override
    public DiscordPagination add(final String... messages) {
        this.pages.addAll(
                Arrays.stream(messages)
                        .map(message -> new MessageBuilder().setContent(message).build())
                        .toList());
        return this;
    }

    @Override
    public DiscordPagination add(final Text... messages) {
        this.pages.addAll(
                Arrays.stream(messages)
                        .map(message -> new MessageBuilder().setContent(message.toStringValue()).build())
                        .toList());
        return this;
    }

    @Override
    public DiscordPagination add(final MessageTemplate... templates) {
        for (final MessageTemplate template : templates) {
            this.add(template.message());
        }
        return this;
    }

    @Override
    public Collection<Object> pages() {
        return HartshornUtils.asUnmodifiableCollection(this.pages);
    }
}
