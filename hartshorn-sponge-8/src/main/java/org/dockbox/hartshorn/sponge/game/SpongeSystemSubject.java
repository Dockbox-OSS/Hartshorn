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

package org.dockbox.hartshorn.sponge.game;

import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;

import javax.inject.Inject;

import lombok.Getter;

public class SpongeSystemSubject extends SystemSubject {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void execute(final String command) {
        try {
            Sponge.server().commandManager().process(Sponge.systemSubject(), command);
        }
        catch (final CommandException e) {
            Except.handle(e);
        }
    }

    @Override
    public void send(final Text text) {
        Sponge.systemSubject().sendMessage(SpongeAdapter.toSponge(text));
    }

    @Override
    public void sendWithPrefix(final Text text) {
        final Text message = Text.of(DefaultResources.instance(this.applicationContext()).prefix(), text);
        Sponge.systemSubject().sendMessage(SpongeAdapter.toSponge(message));
    }

    @Override
    public void send(final Pagination pagination) {
        SpongeAdapter.toSponge(pagination).sendTo(Sponge.systemSubject());
    }
}
