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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandParser;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.context.Context;

import java.lang.reflect.Method;
import java.util.List;

public interface CommandExecutorContext extends Context {

    CommandExecutor executor();
    boolean accepts(String command);
    String strip(String command, boolean parentOnly);
    List<String> aliases();
    Class<?> parent();
    Method method();
    List<String> suggestions(CommandSource source, String command, CommandParser parser);
}
