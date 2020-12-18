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

package org.dockbox.selene.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.core.events.discord.DiscordCommandContext;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.text.Text;

public interface DiscordUtils {

    Exceptional<JDA> getJDA();

    Exceptional<TextChannel> getGlobalTextChannel();

    boolean checkMessageExists(String messageId, String channelId);

    Exceptional<Category> getLoggingCategory();

    Exceptional<Guild> getGuild();

    void sendToTextChannel(Text text, MessageChannel channel);

    void sendToTextChannel(CharSequence text, MessageChannel channel);

    void sendToTextChannel(ResourceEntry text, MessageChannel channel);

    void sendToUser(Text text, User user);

    void sendToUser(CharSequence text, User user);

    void sendToUser(ResourceEntry text, User user);

    void registerCommandListener(Object instance);

    void post(String command, DiscordCommandContext context);

}
