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
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.discord.templates.Template;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.Text;

public interface DiscordUtils {

    Exceptional<JDA> jda();

    Exceptional<User> bot();

    boolean exists(String messageId, String channelId);

    Exceptional<User> user(long id);

    void send(Text text, MessageChannel channel);

    void send(Message text, MessageChannel channel);

    void send(DiscordPagination pagination, MessageChannel channel);

    void send(Template<?> template, MessageChannel channel);

    void send(Text text, User user);

    void send(Message text, User user);

    void send(DiscordPagination pagination, User user);

    void send(Template<?> template, User user);

}
