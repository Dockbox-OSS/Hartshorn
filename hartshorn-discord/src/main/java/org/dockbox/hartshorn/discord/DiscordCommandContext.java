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

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.di.ContextCarrier;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.text.Text;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiscordCommandContext extends DefaultContext implements ContextCarrier {

    private final User author;
    private final MessageChannel channel;
    private final LocalDateTime timeReceived;
    private final String command;
    private final String[] arguments;
    @Getter private final ApplicationContext applicationContext;

    public void sendToChannel(final Text text) {
        this.applicationContext().get(DiscordUtils.class).sendToTextChannel(text, this.channel());
    }

    public void sendToChannel(final ResourceEntry text) {
        this.applicationContext().get(DiscordUtils.class).sendToTextChannel(text, this.channel());
    }

    public void sendToAuthor(final Text text) {
        this.applicationContext().get(DiscordUtils.class).sendToUser(text, this.author());
    }

    public void sendToAuthor(final ResourceEntry text) {
        this.applicationContext().get(DiscordUtils.class).sendToUser(text, this.author());
    }
}
