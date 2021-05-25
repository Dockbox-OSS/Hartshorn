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

import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.annotations.UseCommands;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.services.ServiceProcessor;
import org.dockbox.selene.util.Reflect;

public class CommandServiceProcessor implements ServiceProcessor<UseCommands> {

    @Override
    public boolean preconditions(Class<?> type) {
        return !Reflect.annotatedMethods(type, Command.class).isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        context.get(CommandBus.class).register(type);
    }

    @Override
    public Class<UseCommands> activator() {
        return UseCommands.class;
    }

}
