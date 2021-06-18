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

package org.dockbox.hartshorn.commands.beta.impl;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutorContext;
import org.dockbox.hartshorn.commands.beta.api.CommandParser;
import org.dockbox.hartshorn.commands.beta.api.ParsedContext;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

@Binds(CommandParser.class)
public class SimpleCommandParser implements CommandParser {

    @Override
    public Exceptional<ParsedContext> parse(String command, CommandSource source, CommandExecutorContext context) {
        final Exceptional<CommandContainerContext> container = context.first(CommandContainerContext.class);
        if (container.absent()) return Exceptional.none();

        final CommandContainerContext containerContext = container.get();

        // TODO: Review (keep in mind this should have already been validated in the executor, here we should only strip the aliases)
        final String alias = command.split(" ")[0];
        if (!containerContext.aliases().contains(alias)) return Exceptional.none();

        // TODO: Implement parsing (reuse DefaultCommandContext for this)
        List<ArgumentConverter<?>> elements = containerContext.elements();

        for (ArgumentConverter<?> element : elements) {
            // TODO: Handle parts
            element.convert(source, "");
        }

        // ... (turn values into args + flags)

        return Exceptional.of(new SimpleParsedContext(command,
                HartshornUtils.emptyList(),
                HartshornUtils.emptyList(),
                source, HartshornUtils.singletonList(containerContext.permission())));
    }


}
