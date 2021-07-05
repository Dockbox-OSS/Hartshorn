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

package org.dockbox.hartshorn.discord;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.discord.annotations.DiscordCommand;
import org.dockbox.hartshorn.discord.annotations.UseDiscordCommands;
import org.dockbox.hartshorn.util.Reflect;

public class DiscordServiceProcessor implements ServiceProcessor<UseDiscordCommands> {
    @Override
    public boolean preconditions(Class<?> type) {
        return !Reflect.methods(type, DiscordCommand.class).isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        Hartshorn.context().get(DiscordUtils.class).registerCommandListener(type);
    }

    @Override
    public Class<UseDiscordCommands> activator() {
        return UseDiscordCommands.class;
    }
}
