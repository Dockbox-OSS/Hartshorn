/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.arguments.ArgumentMatchingFailedException;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.commands.definition.CommandDefinition;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandElementImpl;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.definition.CommandFlagElement;
import org.dockbox.hartshorn.commands.definition.CommandFlagImpl;
import org.dockbox.hartshorn.commands.definition.EnumCommandElement;
import org.dockbox.hartshorn.commands.definition.GroupCommandElement;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple implementation of {@link CommandDefinitionContext}. Creates a definition based on the
 * <a href="https://minecraft.fandom.com/wiki/Module:Command/Syntax">Minecraft Command Syntax</a>.
 * <p>Required arguments are formatted as <code>&lt;name&gt;</code>, optional arguments are formatted
 * as <code>[name]</code>, and flags are formatted as <code>-f</code> (short) or <code>--flag</code>
 * (long).
 * <p>See {@link CommandDefinitionContextImpl#FLAG} for details on the flag pattern, and {@link CommandDefinitionContextImpl#ARGUMENT}
 * for details on the argument pattern. Both flags and arguments can be defined with custom types, which
 * can later be associated with specific {@link ArgumentConverter converters}. Details about the format
 * for this definition are explained at {@link CommandDefinitionContextImpl#ELEMENT_VALUE}. If no
 * explicit type is defined, {@link CommandDefinitionContextImpl#DEFAULT_TYPE} is used.
 */
public class CommandDefinitionContextImpl extends DefaultContext implements CommandDefinitionContext {

    /**
     * Represents the default type for command elements matched by {@link CommandDefinitionContextImpl#FLAG} or
     * {@link CommandDefinitionContextImpl#ARGUMENT}. If no type is defined in those matches, this value is used.
     * 'String' is used as this is the base value provided to Hartshorn, thus requiring no further
     * converting to other data types.
     */
    public static final String DEFAULT_TYPE = "String";

    /**
     * Each matching element represents either a flag or argument, these can then be parsed using
     * {@link CommandDefinitionContextImpl#FLAG} and {@link CommandDefinitionContextImpl#ARGUMENT}.
     */
    private static final Pattern GENERIC_ARGUMENT = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))");

    /**
     * Each matching element represents a flag with either one or two groups. The first group (G1) is
     * required, and indicates the name of the flag. The second group (G2) is optional, and represents
     * the value expected by the flag. G2 is an argument which can be parsed using {@link
     * CommandDefinitionContextImpl#ARGUMENT}.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Without value: -f, --flag
     *   <li>With simple value: -f Type, --flag Type
     * </ul>
     */
    private static final Pattern FLAG = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?");

    /**
     * Each matching element represents an argument with two groups. The first group (G1) indicates
     * whether the argument is required or optional. The second group can either be an argument meta
     * which can be parsed using {@link CommandDefinitionContextImpl#ELEMENT_VALUE}, or a simple value if {@link
     * CommandDefinitionContextImpl#ELEMENT_VALUE} returns no matches. Arguments can be grouped.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Optional without type: [Argument]
     *   <li>Optional with simple value: [Argument{Type}]
     *   <li>Required without type: &lt;Argument&gt;
     *   <li>Required with value is equal in syntax to optional, but wrapped in &lt;&gt;
     *   <li>Argument group: [&lt;Argument&gt; &lt;Argument{Type}&gt;]
     * </ul>
     */
    private static final Pattern ARGUMENT = Pattern.compile("([\\[<])(.+)[\\]>]");

    /**
     * Each matching element represents additional meta information for matching elements of {@link
     * CommandDefinitionContextImpl#ARGUMENT}. Matches contain either one or two groups. If both groups are
     * present, group 1 represents the name of the argument, and group 2 represents the value. If only
     * group 1 is present, it represents the type of the argument and the name is obtained from the
     * argument definition.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Type
     *   <li>Name{Type}
     * </ul>
     */
    private static final Pattern ELEMENT_VALUE = Pattern.compile("(\\w+)(?:\\{([\\w\\.]+)?\\})?");

    private final Command command;
    private final CommandDefinition definition;
    private final ApplicationContext context;
    private final MethodView<?, ?> method;

    public CommandDefinitionContextImpl(final ApplicationContext context, final Command command, final MethodView<?, ?> method) {
        this.command = command;
        this.context = context;
        this.method = method;
        if (!"".equals(this.arguments())) {
            this.definition = this.parseElements(this.arguments());
        }
        else {
            this.definition = new CommandDefinition(true, List.of(), List.of());
        }
    }

    protected CommandDefinition parseElements(final CharSequence arguments) {
        final List<CommandElement<?>> elements = new ArrayList<>();
        final List<CommandFlag> flags = new ArrayList<>();

        final Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(arguments);
        while (genericArgumentMatcher.find()) {

            final String part = genericArgumentMatcher.group();
            final Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                this.context.log().debug("Matched argument definition partial " + part + " as explicit argument");
                final CommandDefinition definition = this.extractArguments(argumentMatcher);
                final List<CommandElement<?>> commandElements = definition.elements();
                if (commandElements.isEmpty()) continue;
                if (commandElements.size() == 1) elements.add(commandElements.get(0));
                else elements.add(new GroupCommandElement(commandElements, definition.optional()));
            }
            else {
                this.context.log().debug("Matched argument definition partial " + part + " as flag");
                final Matcher flagMatcher = FLAG.matcher(part);
                flags.addAll(this.generateFlags(flagMatcher));
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            final CommandElement<?> element = elements.get(i);
            if (element.size() == -1 && i != elements.size() - 1) {
                throw new IllegalArgumentRequestException("Cannot request arguments after joining remaining elements");
            }
        }

        return new CommandDefinition(true, elements, flags);
    }

    private CommandDefinition extractArguments(final MatchResult argumentMatcher) {
        final boolean optional = '[' == argumentMatcher.group(1).charAt(0);
        final String elementValue = argumentMatcher.group(2);

        CommandDefinition definition = this.parseElements(elementValue);

        if (definition.elements().isEmpty() && definition.flags().isEmpty()) {
            final CommandElement<?> element = this.generateElement(argumentMatcher.group(2), optional);
            definition = new CommandDefinition(optional, List.of(element), List.of());
        }

        return definition;
    }

    protected CommandElement<?> generateElement(final String definition, final boolean optional) {
        String type = DEFAULT_TYPE;
        final String name;
        final Matcher elementValue = ELEMENT_VALUE.matcher(definition);
        if (!elementValue.matches() || 0 == elementValue.groupCount())
            this.context.log().warn("Unknown argument specification " + definition + ", use Type or Name{Type}");

        /*
        Group one specifies either the name of the value (if two or more groups are matched), or the type if only one
        group matched.
        */
        if (1 <= elementValue.groupCount()) {
            final String g1 = elementValue.group(1);
            if (1 == elementValue.groupCount()) type = g1;
            name = g1;
        }
        else throw new ArgumentMatchingFailedException("Missing key argument in specification '" + definition + "'");

        /*
        Group two matches the type if two or more groups are present. This overwrites the default value if applicable.
        */
        if (2 <= elementValue.groupCount() && null != elementValue.group(2))
            type = elementValue.group(2);

        this.context.log().debug("Determined type '%s', name '%s' for %s argument (definition: %s)".formatted(type, name, optional ? "optional" : "required", definition));
        return this.lookupElement(type, name, optional);
    }

    private <E extends Enum<E>> CommandElement<?> lookupElement(final String type, final String name, final boolean optional) {
        final Result<ArgumentConverter<?>> converter = this.context
                .first(ArgumentConverterContext.class)
                .flatMap(context -> context.converter(type.toLowerCase()));
        if (converter.present()) {
            this.context.log().debug("Found converter for element type " + type);
            return new CommandElementImpl<>(converter.get(), name, optional, converter.get().size());
        }
        else {
            final TypeView<?> lookup = this.context.environment().introspect(type);
            if (lookup.isVoid()) {
                this.context.log().error("No argument of type `" + type + "` can be read");
                return null;
            }

            if (lookup.isEnum()) {
                this.context.log().debug(type + " is an enum, creating explicit enum element.");
                return EnumCommandElement.of(name, (TypeView<E>) lookup, optional);
            }
            else {
                this.context.log().warn("Type '" + type.toLowerCase() + "' is not supported, using default value");
                return this.lookupElement(DEFAULT_TYPE, name, optional);
            }
        }
    }

    private List<CommandFlag> generateFlags(final Matcher flagMatcher) {
        final List<CommandFlag> flags = new ArrayList<>();
        if (flagMatcher.matches()) {
            flags.add(this.parseFlag(
                    flagMatcher.group(1),
                    flagMatcher.group(2)
            ));
        }
        return flags;
    }

    private CommandFlag parseFlag(final String name, final String type) {
        if (null == type) {
            this.context.log().debug("Determined flag definition for '%s'".formatted(name));
            return new CommandFlagImpl(name);
        }
        else {
            this.context.log().debug("Determined flag definition with type '%s' for '%s'".formatted(type, name));
            return new CommandFlagElement<>(this.lookupElement(type, name, true));
        }
    }

    @Override
    public boolean matches(final String command) {
        for (final String candidate : this.aliases()) {
            if (command.startsWith(candidate)) {
                if (!command.contains(" ")) return this.requiredElements().isEmpty();
                else return true;
            }
        }
        return false;
    }

    private List<CommandElement<?>> requiredElements() {
        return this.elements().stream()
                .filter(commandElement -> !commandElement.optional())
                .toList();
    }

    @Override
    public List<String> aliases() {
        final String[] command = this.command.value();
        if (command.length == 0 || (command.length == 1 && "".equals(command[0]))) {
            return Collections.singletonList(this.method.name());
        }
        return List.of(command);
    }

    @Override
    public String arguments() {
        return this.command.arguments();
    }

    @Override
    public Class<?> parent() {
        return this.command.parent();
    }

    @Override
    public List<CommandElement<?>> elements() {
        return this.definition.elements();
    }

    @Override
    public List<CommandFlag> flags() {
        return this.definition.flags();
    }

    @Override
    public Result<CommandFlag> flag(final String name) {
        for (final CommandFlag flag : this.flags()) {
            if (flag.name().equals(name)) return Result.of(flag);
        }
        return Result.empty();
    }
}
