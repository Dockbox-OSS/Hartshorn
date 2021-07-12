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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.events.RegisteredCommandsEvent;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.preload.Preloadable;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.util.Reflect;

@Service(activators = UseBootstrap.class)
@Posting(RegisteredCommandsEvent.class)
public class CommandServiceScanner implements Preloadable {

    @PostBootstrap
    public void preload() {
        final CommandGateway gateway = Hartshorn.context().get(CommandGateway.class);
        for (ComponentContainer container : Hartshorn.context().locator().containers()) {
            if (!Reflect.methods(container.type(), Command.class).isEmpty()) {
                gateway.register(container.type());
            }
        }

        for (Class<? extends CommandExecutorExtension> extension : Reflect.children(CommandExecutorExtension.class)) {
            gateway.add(Hartshorn.context().get(extension));
        }

        new RegisteredCommandsEvent().post();
    }

}
