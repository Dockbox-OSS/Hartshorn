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

package org.dockbox.selene.api.server;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.objects.targets.MessageReceiver;
import org.dockbox.selene.api.util.Reflect;

/**
 * Low-level interface, used by the default integrated server module as indicated by the mappings
 * provided by the platform implementation. Used to access the module when {@link Selene} is used in
 * a {@link Reflect#getModule(Class)} method call.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface IntegratedModule {

    @Command(aliases = "confirm", usage = "confirm <cooldownId{String}>")
    void confirm(MessageReceiver src, CommandContext ctx);
}
