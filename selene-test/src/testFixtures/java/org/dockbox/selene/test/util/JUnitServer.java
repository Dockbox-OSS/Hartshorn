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

package org.dockbox.selene.test.util;

import org.dockbox.selene.api.domain.AbstractIdentifiable;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.commands.CommandBus;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.context.CommandParameter;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.DefaultServerResources;
import org.dockbox.selene.server.Server;
import org.dockbox.selene.test.TestResources;

import javax.inject.Inject;

public class JUnitServer implements Server {

    @Inject
    private DefaultServerResources resources;

    @Override
    public void confirm(MessageReceiver src, CommandContext ctx) {
        if (!(src instanceof AbstractIdentifiable)) {
            src.send(this.resources.getWrongSource());
            return;
        }
        Exceptional<CommandParameter<String>> optionalCooldownId = ctx.argument("cooldownId");

        // UUID is stored by the command executor to ensure runnables are not called by other sources. The uuid
        // argument here is just a confirmation that the source is correct.
        optionalCooldownId.present(cooldownId -> {
            String cid = cooldownId.getValue();
            Provider.provide(CommandBus.class).confirmCommand(cid).absent(() ->
                    src.send(TestResources.SERVER$CONFIRMED));
        }).absent(() -> src.send(TestResources.SERVER$NOT_CONFIRMED));
    }
}
