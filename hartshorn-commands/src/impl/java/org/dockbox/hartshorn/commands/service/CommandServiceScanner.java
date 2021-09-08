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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.boot.annotations.PostBootstrap;
import org.dockbox.hartshorn.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;

@Service(activators = UseBootstrap.class)
public class CommandServiceScanner {

    @PostBootstrap
    public void preload(final ApplicationContext context) {
        final CommandGateway gateway = context.get(CommandGateway.class);
        for (final ComponentContainer container : context.locator().containers()) {
            if (!container.type().flatMethods(Command.class).isEmpty()) {
                gateway.register(container.type());
            }
        }

        for (final TypeContext<? extends CommandExecutorExtension> extension : context.environment().children(CommandExecutorExtension.class)) {
            gateway.add(context.get(extension));
        }
    }

}
