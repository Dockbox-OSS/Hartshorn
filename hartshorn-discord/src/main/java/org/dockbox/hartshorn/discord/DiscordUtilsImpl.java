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

import com.github.ygimenez.exception.InvalidHandlerException;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.Page;
import com.github.ygimenez.model.Paginator;
import com.github.ygimenez.model.PaginatorBuilder;
import com.github.ygimenez.type.PageType;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.discord.templates.MessageTemplate;
import org.dockbox.hartshorn.discord.templates.Template;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

@Binds(DiscordUtils.class)
public class DiscordUtilsImpl implements DiscordUtils {

    @Inject
    private JDA jda;

    @Override
    public Exceptional<JDA> jda() {
        return Exceptional.of(this.jda);
    }

    @Override
    public Exceptional<User> bot() {
        return this.jda().map(JDA::getSelfUser);
    }

    @Override
    public boolean exists(final String messageId, final String channelId) {
        return this.jda().map(jda -> {
            final TextChannel channel = jda.getTextChannelById(channelId);
            if (null == channel) return false;
            final net.dv8tion.jda.api.entities.Message message = channel.retrieveMessageById(messageId).complete();
            return null != message;
        }).or(false);
    }

    @Override
    public Exceptional<User> user(long id) {
        return this.jda().map(jda -> jda.retrieveUserById(id).complete());
    }

    @Override
    public void send(@NotNull final Text text, @NotNull final MessageChannel channel) {
        DiscordUtilsImpl.send(text.toPlain(), channel);
    }

    @Override
    public void send(@NotNull final Message text, @NotNull final MessageChannel channel) {
        DiscordUtilsImpl.send(text.plain(), channel);
    }

    @Override
    public void send(final DiscordPagination pagination, final MessageChannel channel) {
        this.jda().present(jda -> {
            try {
                if (pagination.pages().isEmpty()) return;

                final Paginator paginator = PaginatorBuilder.createPaginator()
                        .setHandler(jda)
                        .shouldRemoveOnReact(false)
                        .build();
                Pages.activate(paginator);

                final List<Page> pages = pagination.pages().stream()
                        .map(page -> {
                            if (page instanceof net.dv8tion.jda.api.entities.Message) {
                                return new Page(PageType.TEXT, page);
                            }
                            else if (page instanceof MessageEmbed) {
                                return new Page(PageType.EMBED, page);
                            }
                            else throw new IllegalArgumentException("Pages of type '" + page.getClass().getName() + "' are not supported");
                        }).toList();

                channel.sendMessage((net.dv8tion.jda.api.entities.Message) pages.get(0).getContent()).queue(success -> Pages.paginate(success, pages));
            }
            catch (final InvalidHandlerException e) {
                Except.handle(e);
            }
        });
    }

    @Override
    public void send(final Template<?> template, final MessageChannel channel) {
        if (template instanceof MessageTemplate)
            channel.sendMessage(((MessageTemplate) template).message()).queue();
    }

    @Override
    public void send(@NotNull final Text text, @NotNull final User user) {
        DiscordUtilsImpl.send(text.toPlain(), user);
    }

    @Override
    public void send(@NotNull final Message text, @NotNull final User user) {
        DiscordUtilsImpl.send(text.plain(), user);
    }

    @Override
    public void send(final DiscordPagination pagination, final User user) {
        user.openPrivateChannel().queue(privateChannel -> this.send(pagination, privateChannel));
    }

    @Override
    public void send(final Template<?> template, final User user) {
        user.openPrivateChannel().queue(channel -> {
            if (template instanceof MessageTemplate)
                channel.sendMessage(((MessageTemplate) template).message());
        });
    }

    public static void send(@NotNull final CharSequence text, @NotNull final User user) {
        send(text, user.openPrivateChannel().complete());
    }

    private static void send(@NotNull final CharSequence text, @NotNull final MessageChannel channel) {
        channel.sendMessage(text).queue();
    }
}
