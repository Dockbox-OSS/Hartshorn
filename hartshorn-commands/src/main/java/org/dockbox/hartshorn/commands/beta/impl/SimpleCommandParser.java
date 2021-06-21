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
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.FakeResource;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.commands.beta.api.CommandElement;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutorContext;
import org.dockbox.hartshorn.commands.beta.api.CommandFlag;
import org.dockbox.hartshorn.commands.beta.api.CommandParser;
import org.dockbox.hartshorn.commands.beta.api.CommandContext;
import org.dockbox.hartshorn.commands.beta.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.context.CommandParameter;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Binds(CommandParser.class)
public class SimpleCommandParser implements CommandParser {

    private static final Pattern FLAG = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?");

    @Override
    public Exceptional<CommandContext> parse(String command, CommandSource source, CommandExecutorContext context) throws ParsingException {
        final Exceptional<CommandContainerContext> container = context.first(CommandContainerContext.class);
        if (container.absent()) return Exceptional.empty();

        final CommandContainerContext containerContext = container.get();
        List<CommandElement<?>> elements = containerContext.elements();

        final List<CommandParameter<?>> parsedElements = HartshornUtils.emptyList();
        final List<CommandParameter<?>> parsedFlags = HartshornUtils.emptyList();

        // Ensure no aliases are left so they are not accidentally parsed
        String stripped = context.strip(command, false);
        // Strip all flags beforehand so elements can be parsed safely without flag interference
        stripped = this.stripFlags(stripped, parsedFlags, source, containerContext);

        final List<String> tokens = HartshornUtils.asList(stripped.split(" "));
        for (int i = 0; i < elements.size(); i++) {
            CommandElement<?> element = elements.get(i);
            final int size = element.size();
            final int end = size == -1 ? tokens.size() : size;

            if (tokens.size() < size) throw new ParsingException(new FakeResource("Not enough arguments for parameter '" + element.name() + "'"));

            final List<String> elementTokens = tokens.subList(0, end);
            final String token = String.join(" ", elementTokens).trim();
            tokens.removeAll(elementTokens);

            final Exceptional<?> value = element.parse(source, token);
            parsedElements.add(this.getParameter(value, "argument", element.name()));

            if (size == -1) {
                if (i != elements.size() - 1) throw new ParsingException(new FakeResource("Illegal argument definition"));
                break;
            }
        }

        if (!tokens.isEmpty()) throw new ParsingException(new FakeResource("Too many arguments"));

        return Exceptional.of(new SimpleCommandContext(command,
                parsedElements,
                parsedFlags,
                source, HartshornUtils.singletonList(containerContext.permission())));
    }

    private String stripFlags(String command, Collection<CommandParameter<?>> flags, CommandSource source, CommandContainerContext context) throws ParsingException {
        final Matcher matcher = FLAG.matcher(command);
        while (matcher.find()) {
            final String flag = matcher.group().substring(1); // Discard '-' prefix
            String name = flag.split(" ")[0];
            final Exceptional<CommandFlag> commandFlag = context.flag(name);
            if (commandFlag.absent()) throw new ParsingException(new FakeResource("Unknown flag '-" + name + "'"));

            final CommandFlag contextFlag = commandFlag.get();
            if (contextFlag.value()) {
                if (contextFlag instanceof CommandFlagElement) {
                    final List<String> tokens = HartshornUtils.asList(command.split(" "));
                    final int size = ((CommandFlagElement<?>) contextFlag).size();
                    final int flagIndex = tokens.indexOf('-' + name);
                    final int i = flagIndex + 1;
                    final int end = size == -1 ? tokens.size() : i + size;

                    String token = String.join(" ", tokens.subList(i, end)).trim();

                    final Exceptional<?> value = ((CommandFlagElement<?>) contextFlag).parse(source, token);
                    flags.add(this.getParameter(value, "flag", name));
                    command = command.replace('-' + name + ' ' + token, "");
                }
            } else {
                flags.add(new CommandParameter<>(null, name));
                command = command.replace('-' + name, "");
            }
        }
        return command.trim();
    }

    private CommandParameter<?> getParameter(Exceptional<?> value, String elementType, String elementName) throws ParsingException {
        if (value.absent()) {
            ResourceEntry resource = new FakeResource("Could not parse " + elementType + " '" + elementName + "'");
            throw value.caught() ? new ParsingException(resource, value.error()) : new ParsingException(resource);
        } else {
            return new CommandParameter<>(value.get(), elementName);
        }
    }

}
