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
import org.dockbox.hartshorn.api.i18n.entry.FakeResource;
import org.dockbox.hartshorn.commands.annotations.Cooldown;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.UUID;

public class CooldownExtension implements CommandExecutorExtension {

    @Override
    public ExtensionResult execute(CommandContext context, CommandExecutorContext executorContext) {
        final CommandSource sender = context.getSender();
        if (!(sender instanceof Identifiable)) return ExtensionResult.accept();

        String id = getRegistrationId((Identifiable) sender, context);
        if (HartshornUtils.isInCooldown(id)) return ExtensionResult.reject(new FakeResource("You are in cooldown!"));
        else {
            Cooldown cooldown = executorContext.method().getAnnotation(Cooldown.class);
            HartshornUtils.cooldown(id, cooldown.duration(), cooldown.unit());
            return ExtensionResult.accept();
        }
    }

    @Override
    public boolean extend(CommandExecutorContext context) {
        return context.method().isAnnotationPresent(Cooldown.class);
    }

    private static String getRegistrationId(Identifiable sender, CommandContext context) {
        UUID uuid = sender.getUniqueId();
        String alias = context.alias();
        return uuid + "$" + alias;
    }
}
