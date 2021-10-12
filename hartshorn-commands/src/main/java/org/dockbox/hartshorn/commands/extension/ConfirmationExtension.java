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

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.WithConfirmation;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.i18n.text.actions.HoverAction;

import javax.inject.Inject;

/**
 * Extends a command by requiring a confirmation action to be performed. This
 * prevents the executor from directly executing the command until the action is
 * performed. Requires the presence of {@link WithConfirmation} on the command.
 */
public class ConfirmationExtension implements CommandExecutorExtension {

    @Inject
    private CommandResources resources;

    @Override
    public ExtensionResult execute(final CommandContext context, final CommandExecutorContext executorContext) {
        final CommandSource sender = context.source();
        if (!(sender instanceof Identifiable)) return ExtensionResult.accept();

        final Runnable action = () -> executorContext.executor().execute(context);
        final Text confirmationText = this.resources.confirmCommand().translate(sender).asText();
        final Text confirmationHover = this.resources.confirmCommandHover().translate(sender).asText();

        confirmationText
                .onHover(HoverAction.showText(confirmationHover))
                .onClick(ClickAction.executeCallback(target -> action.run()));

        sender.send(confirmationText);

        return ExtensionResult.reject(this.resources.confirmCommand(), false);
    }

    @Override
    public boolean extend(final CommandExecutorContext context) {
        return context.element().annotation(WithConfirmation.class).present();
    }
}
