/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.server;

import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.objects.targets.MessageReceiver;

/**
 * Low-level interface, used by the default IntegratedExtension as indicated by the mappings provided by the platform
 * implementation. Used to access the extension when {@link Selene} is used
 * in a {@link org.dockbox.selene.core.SeleneUtils#getExtension(Class) SeleneUtils' getExtension} method call.
 */
public interface IntegratedExtension {

    @Command(aliases = "confirm", usage = "confirm <cooldownId{String}>")
    void confirm(MessageReceiver src, CommandContext ctx);

}
