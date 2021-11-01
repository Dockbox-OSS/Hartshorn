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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.core.domain.Identifiable;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.Languages;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public abstract class SystemSubject implements CommandSource, Identifiable {

    @Inject
    private ApplicationContext context;

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final UUID UNIQUE_ID = new UUID(0, 0);

    public static SystemSubject instance(final ApplicationContext context) {
        return context.get(SystemSubject.class);
    }

    @Override
    public Language language() {
        return Languages.EN_US;
    }

    @Override
    public void language(final Language language) {
        // Nothing happens
    }

    @Override
    public void send(@NotNull final Message text) {
        final Text formattedValue = text.translate().asText();
        this.send(formattedValue);
    }

    @Override
    public void sendWithPrefix(@NotNull final Message text) {
        final Text formattedValue = text.translate().asText();
        this.sendWithPrefix(formattedValue);
    }

    @Override
    public UUID uniqueId() {
        return UNIQUE_ID;
    }

    @Override
    public String name() {
        return "System";
    }

    @Override
    public void execute(final String command) {
        try {
            this.context.get(CommandGateway.class).accept(this, command);
        }
        catch (final ParsingException e) {
            Except.handle(e);
        }
    }
}
