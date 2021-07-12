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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.discord.templates.MessageTemplate;

import java.util.Collection;

public interface DiscordPagination {

    static DiscordPagination create() {
        return Hartshorn.context().get(DiscordPagination.class);
    }

    void send(MessageChannel channel);

    void send(User user);

    DiscordPagination add(Message... message);

    DiscordPagination add(MessageEmbed... embed);

    DiscordPagination add(String... message);

    DiscordPagination add(Text... message);

    DiscordPagination add(MessageTemplate... template);

    Collection<Object> pages();
}
