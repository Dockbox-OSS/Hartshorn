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
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.api.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.annotations.WithConfirmation;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.util.Reflect;

public class ConfirmationExtension implements CommandExecutorExtension {

    @Wired
    private CommandResources resources;

    @Override
    public ExtensionResult execute(CommandContext context, CommandExecutorContext executorContext) {
        final CommandSource sender = context.getSender();
        if (!(sender instanceof Identifiable)) return ExtensionResult.accept();

        final String id = this.id((Identifiable) sender, context);

        Runnable action = () -> executorContext.executor().execute(context);
        final Text confirmationText = this.resources.getConfirmCommand().translate(sender).asText();
        final Text confirmationHover = this.resources.getConfirmCommandHover().translate(sender).asText();

        confirmationText
                .onHover(HoverAction.showText(confirmationHover))
                .onClick(ClickAction.executeCallback(target -> action.run()));

        sender.send(confirmationText);

        return ExtensionResult.reject(this.resources.getConfirmCommand(), false);
    }

    @Override
    public boolean extend(CommandExecutorContext context) {
        return Reflect.annotation(context.method(), WithConfirmation.class).present();
    }
}
