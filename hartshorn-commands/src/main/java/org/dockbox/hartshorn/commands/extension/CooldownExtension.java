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
import org.dockbox.hartshorn.commands.annotations.Cooldown;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import javax.inject.Inject;

/**
 * Extends a command by providing a cooldown on its execution. If a command is
 * performed multiple times its execution is cancelled if it is repeated too
 * quickly. The delay between commands is defined by a {@link Cooldown}
 * decorator on the command.
 */
public class CooldownExtension implements CommandExecutorExtension {

    @Inject
    private CommandResources resources;

    @Override
    public ExtensionResult execute(final CommandContext context, final CommandExecutorContext executorContext) {
        final CommandSource sender = context.source();
        if (!(sender instanceof Identifiable)) return ExtensionResult.accept();

        final String id = this.id((Identifiable) sender, context);
        if (HartshornUtils.inCooldown(id)) {
            context.applicationContext().log().debug("Executor with ID '%s' is in active cooldown, rejecting command execution of %s".formatted(id, context.command()));
            return ExtensionResult.reject(this.resources.cooldownActive());
        }
        else {
            final Cooldown cooldown = executorContext.element().annotation(Cooldown.class).get();
            HartshornUtils.cooldown(id, cooldown.duration(), cooldown.unit());
            return ExtensionResult.accept();
        }
    }

    @Override
    public boolean extend(final CommandExecutorContext context) {
        return context.element().annotation(Cooldown.class).present();
    }
}
