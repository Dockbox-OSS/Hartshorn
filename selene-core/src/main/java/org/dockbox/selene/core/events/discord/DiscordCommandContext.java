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

package org.dockbox.selene.core.events.discord;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;

import java.time.LocalDateTime;

public class DiscordCommandContext {

    private final User author;
    private final MessageChannel channel;
    private final LocalDateTime timeReceived;
    private final String command;
    private final String[] arguments;

    public DiscordCommandContext(User author, MessageChannel channel, LocalDateTime timeReceived, String command, String[] arguments) {
        this.author = author;
        this.channel = channel;
        this.timeReceived = timeReceived;
        this.command = command;
        this.arguments = arguments;
    }

    public void sendToChannel(Text text) {
        SeleneUtils.INJECT.getInstance(DiscordUtils.class).sendToTextChannel(text, this.getChannel());
    }

    public MessageChannel getChannel() {
        return this.channel;
    }

    public void sendToChannel(CharSequence text) {
        SeleneUtils.INJECT.getInstance(DiscordUtils.class).sendToTextChannel(text, this.getChannel());
    }

    public void sendToChannel(ResourceEntry text) {
        SeleneUtils.INJECT.getInstance(DiscordUtils.class).sendToTextChannel(text, this.getChannel());
    }

    public void sendToAuthor(Text text) {
        SeleneUtils.INJECT.getInstance(DiscordUtils.class).sendToUser(text, this.getAuthor());
    }

    public User getAuthor() {
        return this.author;
    }

    public void sendToAuthor(CharSequence text) {
        SeleneUtils.INJECT.getInstance(DiscordUtils.class).sendToUser(text, this.getAuthor());
    }

    public void sendToAuthor(ResourceEntry text) {
        SeleneUtils.INJECT.getInstance(DiscordUtils.class).sendToUser(text, this.getAuthor());
    }

    public LocalDateTime getTimeReceived() {
        return this.timeReceived;
    }

    public String getCommand() {
        return this.command;
    }

    public String[] getArguments() {
        return this.arguments;
    }
}
