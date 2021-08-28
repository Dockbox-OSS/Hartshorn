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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.discord.templates.Template;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.text.Text;

@Service
public interface DiscordUtils {

    Exceptional<JDA> jda();

    Exceptional<TextChannel> globalChannel();

    boolean checkMessageExists(String messageId, String channelId);

    Exceptional<Category> loggingCategory();

    Exceptional<Guild> guild();

    void sendToTextChannel(Text text, MessageChannel channel);

    void sendToTextChannel(ResourceEntry text, MessageChannel channel);

    void sendToTextChannel(DiscordPagination pagination, MessageChannel channel);

    void sendToTextChannel(Template<?> template, MessageChannel channel);

    void sendToUser(Text text, User user);

    void sendToUser(ResourceEntry text, User user);

    void sendToUser(DiscordPagination pagination, User user);

    void sendToUser(Template<?> template, User user);

    void registerCommandListener(TypeContext<?> type);

    void post(String command, DiscordCommandContext context);
}
