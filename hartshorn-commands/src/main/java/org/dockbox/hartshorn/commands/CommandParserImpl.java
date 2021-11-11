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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandContextImpl;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.definition.CommandFlagElement;
import org.dockbox.hartshorn.commands.definition.CommandPartial;
import org.dockbox.hartshorn.commands.definition.GroupCommandElement;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Simple implementation of {@link CommandParser}.
 */
@Binds(CommandParser.class)
public class CommandParserImpl implements CommandParser {

    // Note the difference between this and SimpleCommandContainerContext.FLAG, here a space is expected before the flag
    // to indicate it is a single element and not part of a piece of text.
    private static final Pattern FLAG = Pattern.compile(" -(-?\\w+)(?: ([^ -]+))?");

    @Inject
    private CommandResources resources;

    @Override
    public Exceptional<CommandContext> parse(final String command, final CommandSource source, final CommandExecutorContext context) throws ParsingException {
        final ApplicationContext applicationContext = context.applicationContext();
        final Exceptional<CommandDefinitionContext> container = context.first(CommandDefinitionContext.class);
        if (container.absent()) return Exceptional.empty();

        final CommandDefinitionContext containerContext = container.get();
        final List<CommandElement<?>> elements = containerContext.elements();

        final List<CommandParameter<?>> parsedElements = HartshornUtils.emptyList();
        final List<CommandParameter<?>> parsedFlags = HartshornUtils.emptyList();

        // Ensure no aliases are left, so they are not accidentally parsed
        String stripped = context.strip(command, false);
        // Strip all flags beforehand so elements can be parsed safely without flag interference
        stripped = this.stripFlags(stripped, parsedFlags, source, containerContext);

        final List<String> tokens = HartshornUtils.asList(stripped.split(" "));
        parsedElements.addAll(this.parse(elements, tokens, source));

        applicationContext.log().debug("Parsed %d elements and %d flags for input %s".formatted(parsedElements.size(), parsedFlags.size(), command));

        if (!tokens.isEmpty() && !"".equals(tokens.get(0))) throw new ParsingException(this.resources.tooManyArguments());

        return Exceptional.of(new CommandContextImpl(command,
                parsedElements,
                parsedFlags,
                source,
                source.applicationContext()
        ));
    }

    private List<CommandParameter<?>> parse(final List<CommandElement<?>> elements, final List<String> tokens, final CommandSource source) throws ParsingException {
        final List<CommandParameter<?>> parameters = HartshornUtils.emptyList();
        for (int i = 0; i < elements.size(); i++) {
            final CommandElement<?> element = elements.get(i);
            final int size = element.size();
            final int end = size == -1 ? tokens.size() : size;

            if (tokens.size() < size || end > tokens.size()) {
                if (element.optional()) continue;
                else throw new ParsingException(this.resources.notEnoughParameterArguments(element.name()));
            }

            final List<String> elementTokens = tokens.subList(0, end);
            final String token = String.join(" ", elementTokens).trim();
            tokens.removeAll(elementTokens);

            final Exceptional<?> value = element.parse(source, token);
            parameters.addAll(this.parameter(value, token, "argument", element.name(), element, source));

            if (size == -1) {
                if (i != elements.size() - 1) throw new ParsingException(this.resources.illegalArgumentDefinition());
                break;
            }
        }
        return parameters;
    }

    private String stripFlags(String command, final Collection<CommandParameter<?>> flags, final CommandSource source, final CommandDefinitionContext context) throws ParsingException {
        final Matcher matcher = FLAG.matcher(command);

        while (matcher.find()) {
            final String flag = matcher.group().trim();
            final String nameUntrimmed = flag.split(" ")[0];
            final String name = HartshornUtils.trimWith('-', nameUntrimmed);
            final Exceptional<CommandFlag> commandFlag = context.flag(name);
            if (commandFlag.absent()) throw new ParsingException(this.resources.unknownFlag(name));

            final CommandFlag contextFlag = commandFlag.get();
            if (contextFlag.value()) {
                if (contextFlag instanceof CommandFlagElement) {
                    final List<String> tokens = HartshornUtils.asList(command.split(" "));
                    final int size = ((CommandFlagElement<?>) contextFlag).size();
                    final int flagIndex = tokens.indexOf(nameUntrimmed);
                    final int i = flagIndex + 1;
                    final int end = size == -1 ? tokens.size() : i + size;

                    final String token = String.join(" ", tokens.subList(i, end)).trim();

                    final Exceptional<?> value = ((CommandFlagElement<?>) contextFlag).parse(source, token);
                    flags.addAll(this.parameter(value, token, "flag", name, contextFlag, source));
                    command = command.replace(flag, "");
                }
            }
            else {
                flags.add(new CommandParameter<>(null, name));
                command = command.replace(flag, "");
            }
        }
        return command.trim();
    }

    private Collection<CommandParameter<?>> parameter(final Exceptional<?> value, final String token, final String elementType, final String elementName, final CommandPartial partial, final CommandSource source) throws ParsingException {
        if (value.absent()) {
            final Message resource = this.resources.couldNotParse(elementType, elementName);
            throw value.caught() ? new ParsingException(resource, value.error()) : new ParsingException(resource);
        }
        else {
            if (partial instanceof GroupCommandElement) {
                //noinspection unchecked
                final List<CommandElement<?>> elements = (List<CommandElement<?>>) value.get();
                final List<String> tokens = HartshornUtils.asList(token.split(" "));
                return this.parse(elements, tokens, source);
            }
            else {
                return HartshornUtils.singletonList(new CommandParameter<>(value.get(), elementName));
            }
        }
    }

}
