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

package org.dockbox.selene.commands;

import org.dockbox.selene.api.annotations.PostBootstrap;
import org.dockbox.selene.api.annotations.UseBootstrap;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.services.ServiceContainer;
import org.dockbox.selene.di.services.ServiceLocator;

@Service(activator = UseBootstrap.class)
public class CommandRegistrationService {

    @PostBootstrap
    public void registerServices(ApplicationContext context) {
        final ServiceLocator locator = context.locator();
        final CommandBus bus = context.get(CommandBus.class);
        for (ServiceContainer container : locator.containers()) {
            bus.register(container.getType());
        }
    }
}
