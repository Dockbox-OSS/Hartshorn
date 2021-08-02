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
import org.dockbox.hartshorn.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.server.minecraft.Console;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;

public class SpongeConsole extends Console {

    @Override
    public void execute(String command) {
        try {
            Sponge.server().commandManager().process(Sponge.systemSubject(), command);
        }
        catch (CommandException e) {
            Except.handle(e);
        }
    }

    @Override
    public void send(Text text) {
        Sponge.systemSubject().sendMessage(SpongeConvert.toSponge(text));
    }

    @Override
    public void sendWithPrefix(Text text) {
        final Text message = Text.of(DefaultResources.instance().prefix(), text);
        Sponge.systemSubject().sendMessage(SpongeConvert.toSponge(message));
    }

    @Override
    public void send(Pagination pagination) {
        SpongeConvert.toSponge(pagination).sendTo(Sponge.systemSubject());
    }
}
