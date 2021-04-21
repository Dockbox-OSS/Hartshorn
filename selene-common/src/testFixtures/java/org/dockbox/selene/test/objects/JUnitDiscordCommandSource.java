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

package org.dockbox.selene.test.objects;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.selene.api.command.source.DiscordCommandSource;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.text.pagination.Pagination;

public class JUnitDiscordCommandSource implements DiscordCommandSource {

    private final TextChannel textChannel;

    @AssistedInject
    public JUnitDiscordCommandSource(@Assisted TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    @Override
    public void execute(String command) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void send(ResourceEntry text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void send(Text text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void sendWithPrefix(ResourceEntry text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void sendWithPrefix(Text text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void send(Pagination pagination) {
        // TODO: Test implementation, mocking client?
    }
}
