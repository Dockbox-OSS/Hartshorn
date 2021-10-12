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

import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.commands.definition.CommandDefinition;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandElementImpl;
import org.dockbox.hartshorn.commands.definition.CommandElements;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.definition.CommandFlagElement;
import org.dockbox.hartshorn.commands.definition.CommandFlagImpl;
import org.dockbox.hartshorn.commands.definition.GroupCommandElement;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.util.HartshornUtils;

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
@SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
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
     * the value expected by the flag. G2 is a argument which can be parsed using {@link
     * CommandDefinitionContextImpl#ARGUMENT}.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Without value: -f, --flag
     *   <li>With simple value: -f Type, --flag Type
     *   <li>With permission value: -f Type:Permission, --flag Type:Permission
     * </ul>
     */
    private static final Pattern FLAG = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?");

    /**
     * Each matching element represents a argument with two groups. The first group (G1) indicates
     * whether the argument is required or optional. The second group can either be a argument meta
     * which can be parsed using {@link CommandDefinitionContextImpl#ELEMENT_VALUE}, or a simple value if {@link
     * CommandDefinitionContextImpl#ELEMENT_VALUE} returns no matches. Arguments can be grouped.
     *
     * <p>Syntax:
     *
     * <ul>
     *   <li>Optional without type: [Argument]
     *   <li>Optional with simple value: [Argument{Type}]
     *   <li>Optional with permission value: [Argument{Type:Permission}]
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
     *   <li>Name{Type:Permission}
     * </ul>
     */
    private static final Pattern ELEMENT_VALUE = Pattern.compile("(\\w+)(?:\\{([\\w\\.]+)(?::([\\w\\.]+))?\\})?");

    private final Permission permission;
    private final Command command;
    private final CommandDefinition definition;
    private final ApplicationContext context;
    private final MethodContext<?, ?> method;

    public CommandDefinitionContextImpl(final ApplicationContext context, final Command command, final MethodContext<?, ?> method) {
        this.command = command;
        this.context = context;
        this.method = method;
        this.permission = this.getOrDefault();
        if (!"".equals(this.arguments())) {
            this.definition = this.parseElements(this.arguments(), this.permission);
        }
        else {
            this.definition = new CommandDefinition(true, HartshornUtils.emptyList(), HartshornUtils.emptyList());
        }
    }

    protected Permission getOrDefault() {
        String raw = this.command.permission();

        if ("".equals(raw)) {
            raw = Hartshorn.PROJECT_ID + '.'
                    + Bindings.serviceId(this.context, this.parent()).replaceAll("-", ".") + '.'
                    + this.aliases().get(0);
        }

        return Permission.of(this.context, raw);
    }

    protected CommandDefinition parseElements(final CharSequence arguments, final Permission permission) {
        final List<CommandElement<?>> elements = HartshornUtils.emptyList();
        final List<CommandFlag> flags = HartshornUtils.emptyList();

        final Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(arguments);
        while (genericArgumentMatcher.find()) {

            final String part = genericArgumentMatcher.group();
            final Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                final CommandDefinition definition = this.extractArguments(argumentMatcher, permission);
                final List<CommandElement<?>> commandElements = definition.elements();
                if (commandElements.isEmpty()) continue;
                if (commandElements.size() == 1) elements.add(commandElements.get(0));
                else elements.add(new GroupCommandElement(commandElements, definition.optional()));
            }
            else {
                final Matcher flagMatcher = FLAG.matcher(part);
                flags.addAll(this.generateFlags(flagMatcher, permission));
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            final CommandElement<?> element = elements.get(i);
            if (element.size() == -1 && i != elements.size() - 1) {
                throw new IllegalStateException("Cannot request arguments after joining remaining elements");
            }
        }

        return new CommandDefinition(true, elements, flags);
    }

    private CommandDefinition extractArguments(final MatchResult argumentMatcher, final Permission permission) {
        final boolean optional = '[' == argumentMatcher.group(1).charAt(0);
        final String elementValue = argumentMatcher.group(2);

        CommandDefinition definition = this.parseElements(elementValue, permission);

        if (definition.elements().isEmpty() && definition.flags().isEmpty()) {
            final CommandElement<?> element = this.generateElement(argumentMatcher.group(2), permission, optional);
            definition = new CommandDefinition(optional, HartshornUtils.asList(element), HartshornUtils.emptyList());
        }

        return definition;
    }

    protected CommandElement<?> generateElement(final String definition, final Permission permission, final boolean optional) {
        String type = DEFAULT_TYPE;
        final String name;
        final Matcher elementValue = ELEMENT_VALUE.matcher(definition);
        Permission elementPermission = permission;
        if (!elementValue.matches() || 0 == elementValue.groupCount())
            Except.handle("Unknown argument specification " + definition + ", use Type or Name{Type} or Name{Type:Permission}");

        /*
        Group one specifies either the name of the value (if two or more groups are matched), or the type if only one
        group matched.
        */
        if (1 <= elementValue.groupCount()) {
            final String g1 = elementValue.group(1);
            if (1 == elementValue.groupCount()) type = g1;
            name = g1;
        }
        else throw new IllegalArgumentException("Missing key argument in specification '" + definition + "'");

        /*
        Group two matches the type if two or more groups are present. This overwrites the default value if applicable.
        */
        if (2 <= elementValue.groupCount() && null != elementValue.group(2))
            type = elementValue.group(2);

        /*
        Group three matches the permission if three groups are present. If the third group is not present, the default
        permission is used. Usually the default permission is provided by the original command registration (which
        defaults to HartshornInformation#GLOBAL_OVERRIDE if none is explicitly specified).
        */
        if (3 <= elementValue.groupCount() && null != elementValue.group(3))
            elementPermission = Permission.of(this.context, elementValue.group(3));

        return this.lookupElement(type, name, elementPermission, optional);
    }

    private <E extends Enum<E>> CommandElement<?> lookupElement(final String type, final String name, final Permission permission, final boolean optional) {
        final Exceptional<ArgumentConverter<?>> converter = this.context
                .first(ArgumentConverterContext.class)
                .flatMap(context -> context.converter(type.toLowerCase()));
        if (converter.present()) {
            return new CommandElementImpl<>(converter.get(), name, permission, optional, converter.get().size());
        }
        else {
            final TypeContext<?> lookup = TypeContext.lookup(type);
            if (lookup.isVoid()) {
                Except.handle("No argument of type `" + type + "` can be read");
                return null;
            }

            if (lookup.isEnum()) {
                //noinspection unchecked
                return CommandElements.enumElement(name, permission, (TypeContext<E>) lookup, optional);
            }
            else {
                Hartshorn.log().warn("Type '" + type.toLowerCase() + "' is not supported, using default value");
                return this.lookupElement(DEFAULT_TYPE, name, permission, optional);
            }
        }
    }

    private List<CommandFlag> generateFlags(final Matcher flagMatcher, final Permission defaultPermission) {
        final List<CommandFlag> flags = HartshornUtils.emptyList();
        if (flagMatcher.matches()) {
            flags.add(this.parseFlag(
                    flagMatcher.group(1),
                    flagMatcher.group(2),
                    defaultPermission
            ));
        }
        return flags;
    }

    private CommandFlag parseFlag(String name, final String type, final Permission defaultPermission) {
        if (null == type) {
            final int at;
            /* See syntax definition of DefaultCommandBus#FLAG */
            if (0 <= (at = name.indexOf(':'))) {
                name = name.substring(0, at);
                final String permission = name.substring(at + 1);
                return new CommandFlagImpl(name, Permission.of(this.context, permission));
            }
            else {
                return new CommandFlagImpl(name);
            }
        }
        else {
            if (0 <= name.indexOf(':')) {
                Except.handle("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            return new CommandFlagElement<>(this.lookupElement(type, name, defaultPermission, true));
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
        if (command.length == 0 || (command.length == 1 && command[0].equals(""))) {
            return HartshornUtils.singletonList(this.method.name());
        }
        return HartshornUtils.asUnmodifiableList(command);
    }

    @Override
    public String arguments() {
        return this.command.arguments();
    }

    @Override
    public Permission permission() {
        return this.permission;
    }

    @Override
    public TypeContext<?> parent() {
        return TypeContext.of(this.command.parent());
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
    public Exceptional<CommandFlag> flag(final String name) {
        for (final CommandFlag flag : this.flags()) {
            if (flag.name().equals(name)) return Exceptional.of(flag);
        }
        return Exceptional.empty();
    }
}
