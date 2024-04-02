/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandContextImpl;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.definition.CommandFlagElement;
import org.dockbox.hartshorn.commands.definition.CommandPartial;
import org.dockbox.hartshorn.commands.definition.GroupCommandElement;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

/**
 * Simple implementation of {@link CommandParser}.
 */
public class CommandParserImpl implements CommandParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommandParserImpl.class);

    // Note the difference between this and SimpleCommandContainerContext.FLAG, here a space is expected before the flag
    // to indicate it is a single element and not part of a piece of text.
    private static final Pattern FLAG = Pattern.compile(" -(-?\\w+)(?: ([^ -]+))?");

    private final CommandResources resources;

    @Inject
    public CommandParserImpl(CommandResources resources) {
        this.resources = resources;
    }

    @Override
    public Option<CommandContext> parse(String command, CommandSource source, CommandExecutorContext context) throws ParsingException {
        ApplicationContext applicationContext = context.applicationContext();
        Option<CommandDefinitionContext> container = context.firstContext(CommandDefinitionContext.class);
        if (container.absent()) {
            return Option.empty();
        }

        CommandDefinitionContext containerContext = container.get();
        List<CommandElement<?>> elements = containerContext.elements();


        // Ensure no aliases are left, so they are not accidentally parsed
        String stripped = context.strip(command, false);

        // Strip all flags beforehand so elements can be parsed safely without flag interference
        List<CommandParameter<?>> parsedFlags = new ArrayList<>();
        stripped = this.stripFlags(stripped, parsedFlags, source, containerContext);

        List<String> tokens = new ArrayList<>(Arrays.asList(stripped.split(" ")));
        List<CommandParameter<?>> parsedElements = new ArrayList<>(this.parse(elements, tokens, source));

        LOG.debug("Parsed %d elements and %d flags for input %s".formatted(parsedElements.size(), parsedFlags.size(), command));

        if (!tokens.isEmpty() && !"".equals(tokens.get(0))) {
            throw new ParsingException(this.resources.tooManyArguments());
        }

        return Option.of(new CommandContextImpl(command,
                parsedElements,
                parsedFlags,
                source,
                source.applicationContext()
        ));
    }

    private List<CommandParameter<?>> parse(List<CommandElement<?>> elements, List<String> tokens, CommandSource source) throws ParsingException {
        List<CommandParameter<?>> parameters = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            CommandElement<?> element = elements.get(i);
            int size = element.size();
            int end = size == -1 ? tokens.size() : size;

            if (tokens.size() < size || end > tokens.size()) {
                if (element.optional()) {
                    continue;
                }
                else {
                    throw new ParsingException(this.resources.notEnoughParameterArguments(element.name()));
                }
            }

            List<String> elementTokens = tokens.subList(0, end);
            String token = String.join(" ", elementTokens).trim();
            tokens.removeAll(elementTokens);

            Option<?> value = element.parse(source, token);
            parameters.addAll(this.parameter(value, token, "argument", element.name(), element, source));

            if (size == -1) {
                if (i != elements.size() - 1) {
                    throw new ParsingException(this.resources.illegalArgumentDefinition());
                }
                break;
            }
        }
        return parameters;
    }

    private String stripFlags(String command, Collection<CommandParameter<?>> flags, CommandSource source, CommandDefinitionContext context) throws ParsingException {
        Matcher matcher = FLAG.matcher(command);

        while (matcher.find()) {
            String flag = matcher.group().trim();
            String nameUntrimmed = flag.split(" ")[0];
            String name = StringUtilities.trimWith('-', nameUntrimmed);
            Option<CommandFlag> commandFlag = context.flag(name);
            if (commandFlag.absent()) {
                throw new ParsingException(this.resources.unknownFlag(name));
            }

            CommandFlag contextFlag = commandFlag.get();
            if (contextFlag.value()) {
                if (contextFlag instanceof CommandFlagElement) {
                    List<String> tokens = List.of(command.split(" "));
                    int size = ((CommandElement<?>) contextFlag).size();
                    int flagIndex = tokens.indexOf(nameUntrimmed);
                    int i = flagIndex + 1;
                    int end = size == -1 ? tokens.size() : i + size;

                    String token = String.join(" ", tokens.subList(i, end)).trim();

                    Option<?> value = ((CommandElement<?>) contextFlag).parse(source, token);
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

    private Collection<CommandParameter<?>> parameter(Option<?> value, String token, String elementType, String elementName, CommandPartial partial, CommandSource source) throws ParsingException {
        if (value.absent()) {
            Message resource = this.resources.couldNotParse(elementType, elementName);
            throw new ParsingException(resource);
        }
        else {
            if (partial instanceof GroupCommandElement) {
                List<CommandElement<?>> elements = (List<CommandElement<?>>) value.get();
                List<String> tokens = List.of(token.split(" "));
                return this.parse(elements, tokens, source);
            }
            else {
                return Collections.singletonList(new CommandParameter<>(value.get(), elementName));
            }
        }
    }

}
