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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.commands.context.CommandContainerContext;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.context.SimpleCommandContext;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.definition.CommandFlagElement;
import org.dockbox.hartshorn.commands.definition.CommandPartial;
import org.dockbox.hartshorn.commands.definition.GroupCommandElement;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Binds(CommandParser.class)
public class SimpleCommandParser implements CommandParser {

    // Note the difference between this and SimpleCommandContainerContext.FLAG, here a space is expected before the flag
    // to indicate it is a single element and not part of a piece of text.
    private static final Pattern FLAG = Pattern.compile(" -(-?\\w+)(?: ([^ -]+))?");

    @Wired
    private CommandResources resources;

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
        parsedElements.addAll(this.parse(elements, tokens, source));

        if (!tokens.isEmpty() && !"".equals(tokens.get(0))) throw new ParsingException(this.resources.getTooManyArguments());

        return Exceptional.of(new SimpleCommandContext(command,
                parsedElements,
                parsedFlags,
                source, HartshornUtils.singletonList(containerContext.permission())));
    }

    private List<CommandParameter<?>> parse(List<CommandElement<?>> elements, List<String> tokens, CommandSource source) throws ParsingException {
        List<CommandParameter<?>> parameters = HartshornUtils.emptyList();
        for (int i = 0; i < elements.size(); i++) {
            CommandElement<?> element = elements.get(i);
            final int size = element.size();
            final int end = size == -1 ? tokens.size() : size;

            if (tokens.size() < size || end > tokens.size()) {
                if (element.optional()) continue;
                else throw new ParsingException(this.resources.getNotEnoughParameterArguments(element.name()));
            }

            final List<String> elementTokens = tokens.subList(0, end);
            final String token = String.join(" ", elementTokens).trim();
            tokens.removeAll(elementTokens);

            final Exceptional<?> value = element.parse(source, token);
            parameters.addAll(this.getParameter(value, token, "argument", element.name(), element, source));

            if (size == -1) {
                if (i != elements.size() - 1) throw new ParsingException(this.resources.getIllegalArgumentDefinition());
                break;
            }
        }
        return parameters;
    }

    private String stripFlags(String command, Collection<CommandParameter<?>> flags, CommandSource source, CommandContainerContext context) throws ParsingException {
        final Matcher matcher = FLAG.matcher(command);

        while (matcher.find()) {
            final String flag = matcher.group().trim();
            final String nameUntrimmed = flag.split(" ")[0];
            final String name = HartshornUtils.trimWith('-', nameUntrimmed);
            final Exceptional<CommandFlag> commandFlag = context.flag(name);
            if (commandFlag.absent()) throw new ParsingException(this.resources.getUnknownFlag(name));

            final CommandFlag contextFlag = commandFlag.get();
            if (contextFlag.value()) {
                if (contextFlag instanceof CommandFlagElement) {
                    final List<String> tokens = HartshornUtils.asList(command.split(" "));
                    final int size = ((CommandFlagElement<?>) contextFlag).size();
                    final int flagIndex = tokens.indexOf(nameUntrimmed);
                    final int i = flagIndex + 1;
                    final int end = size == -1 ? tokens.size() : i + size;

                    String token = String.join(" ", tokens.subList(i, end)).trim();

                    final Exceptional<?> value = ((CommandFlagElement<?>) contextFlag).parse(source, token);
                    flags.addAll(this.getParameter(value, token, "flag", name, contextFlag, source));
                    command = command.replace(flag, "");
                }
            } else {
                flags.add(new CommandParameter<>(null, name));
                command = command.replace(flag, "");
            }
        }
        return command.trim();
    }

    private Collection<CommandParameter<?>> getParameter(Exceptional<?> value, String token, String elementType, String elementName, CommandPartial partial, CommandSource source) throws ParsingException {
        if (value.absent()) {
            ResourceEntry resource = this.resources.getCouldNotParse(elementType, elementName);
            throw value.caught() ? new ParsingException(resource, value.error()) : new ParsingException(resource);
        } else {
            if (partial instanceof GroupCommandElement) {
                //noinspection unchecked
                final List<CommandElement<?>> elements = (List<CommandElement<?>>) value.get();
                final List<String> tokens = HartshornUtils.asList(token.split(" "));
                return this.parse(elements, tokens, source);
            } else {
                return HartshornUtils.singletonList(new CommandParameter<>(value.get(), elementName));
            }
        }
    }

}