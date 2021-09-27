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

package org.dockbox.hartshorn.test.objects;

import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.discord.DiscordCommandSource;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;

import javax.inject.Inject;

import lombok.Getter;

public class JUnitDiscordCommandSource implements DiscordCommandSource {

    @Inject
    @Getter
    private ApplicationContext applicationContext;
    private final TextChannel textChannel;

    @Bound
    public JUnitDiscordCommandSource(final TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    @Override
    public void execute(final String command) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Message text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Text text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void sendWithPrefix(final Message text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void sendWithPrefix(final Text text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Pagination pagination) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }
}
