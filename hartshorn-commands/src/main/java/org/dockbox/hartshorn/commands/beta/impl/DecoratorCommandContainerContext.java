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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.commands.beta.api.CommandElement;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.commands.convert.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.values.AbstractFlagCollection;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecoratorCommandContainerContext extends DefaultContext implements CommandContainerContext {

    /**
     * Represents the default type for command elements matched by {@link DecoratorCommandContainerContext#FLAG} or
     * {@link DecoratorCommandContainerContext#ARGUMENT}. If no type is defined in those matches, this value is used.
     * 'String' is used as this is the base value provided to Hartshorn, thus requiring no further
     * converting to other data types.
     */
    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String DEFAULT_TYPE = "String";

    /**
     * Each matching element represents either a flag or argument, these can then be parsed using
     * {@link DecoratorCommandContainerContext#FLAG} and {@link DecoratorCommandContainerContext#ARGUMENT}.
     */
    private static final Pattern GENERIC_ARGUMENT = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))");

    /**
     * Each matching element represents a flag with either one or two groups. The first group (G1) is
     * required, and indicates the name of the flag. The second group (G2) is optional, and represents
     * the value expected by the flag. G2 is a argument which can be parsed using {@link
     * DecoratorCommandContainerContext#ARGUMENT}.
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
     * which can be parsed using {@link DecoratorCommandContainerContext#ELEMENT_VALUE}, or a simple value if {@link
     * DecoratorCommandContainerContext#ELEMENT_VALUE} returns no matches. Arguments can be grouped.
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
     * DecoratorCommandContainerContext#ARGUMENT}. Matches contain either one or two groups. If both groups are
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
    private static final Pattern ELEMENT_VALUE = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?");

    private final Permission permission;
    private final Command command;
    private final List<CommandElement<?>> elements;

    public DecoratorCommandContainerContext(Command command) {
        this.command = command;
        this.permission = this.getPermissionOrDefault();
        if (!"".equals(this.arguments()))
            this.elements = this.parseArgumentElements(this.arguments(), this.permission);
        else this.elements = HartshornUtils.emptyList();
    }
    
    protected Permission getPermissionOrDefault() {
        String raw = this.command.permission();
        if ("".equals(raw)) {
            raw = Hartshorn.PROJECT_ID + '.' + Bindings.serviceId(this.parent()).replaceAll("-", ".");
            // TODO: Extend with executor
//            if (this instanceof MethodCommandContext) {
//                raw += '.' + this.aliases().get(0);
//            }
        }
        return Permission.of(raw);
    }
    
    protected List<CommandElement<?>> parseArgumentElements(CharSequence arguments, Permission permission) {
        List<CommandElement<?>> elements = HartshornUtils.emptyList();
        AbstractFlagCollection<?> flagCollection = null;

        Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(arguments);
        while (genericArgumentMatcher.find()) {

            String part = genericArgumentMatcher.group();
            Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                elements = this.extractArguments(elements, argumentMatcher, permission);
            }
            else {
                Matcher flagMatcher = FLAG.matcher(part);
//                flagCollection = this.getAbstractFlagCollection(flagCollection, flagMatcher, defaultPermission);
            }
        }

        // TODO: Review need
        if (null == flagCollection) return elements;
        else {
//            return flagCollection.buildAndCombines(this.wrapElements(elements));
            return null;
        }
    }

    private List<CommandElement<?>> extractArguments(Collection<CommandElement<?>> elements, MatchResult argumentMatcher, Permission permission) {
        boolean optional = '[' == argumentMatcher.group(1).charAt(0);
        String elementValue = argumentMatcher.group(2);

        List<CommandElement<?>> result = this.parseArgumentElements(elementValue, permission);
        if (result.isEmpty()) {
            CommandElement<?> element = this.generateCommandElement(argumentMatcher.group(2), permission, optional);
            result = HartshornUtils.asList(element);
        }

        return result;
    }

    protected CommandElement<?> generateCommandElement(String definition, Permission permission, boolean optional) {
        String type = "String";
        String name;
        Matcher elementValue = null;
        Permission elementPermission = permission;
        if (!elementValue.matches() || 0 == elementValue.groupCount())
            Except.handle("Unknown argument specification " + definition + ", use Type or Name{Type} or Name{Type:Permission}");

        /*
        Group one specifies either the name of the value (if two or more groups are matched), or the type if only one
        group matched.
        */
        if (1 <= elementValue.groupCount()) {
            String g1 = elementValue.group(1);
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
            elementPermission = Permission.of(elementValue.group(3));

        return this.lookupElement(type, name, elementPermission, optional);
    }

    private <E extends Enum<E>> CommandElement<?> lookupElement(String type, String name, Permission permission, boolean optional) {
        Exceptional<ArgumentConverter<?>> converter = ArgumentConverterRegistry.getOptionalConverter(type.toLowerCase());
        if (converter.present()) {
            return new SimpleCommandElement<>(converter.get(), name, permission, optional);
        } else {
            try {
                Class<?> clazz = Class.forName(type);
                if (clazz.isEnum()) {
                    //noinspection unchecked
                    Class<E> enumType = (Class<E>) clazz;
                    return CommandElements.enumElement(name, permission, enumType, optional);
                }
                else {
                    Hartshorn.log().warn("Type '" + type.toLowerCase() + "' is not supported, using default value");
                    return this.lookupElement(DEFAULT_TYPE, name, permission, optional);
                }
            }
            catch (Exception e) {
                Except.handle("No argument of type `" + type + "` can be read", e);
                return null;
            }
        }
    }

    @Override
    public boolean matches(String command) {
        for (String alias : this.aliases()) {
        }
        // TODO: Implement
        return false;
    }

    @Override
    public List<String> aliases() {
        return HartshornUtils.asUnmodifiableList(this.command.value());
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
    public long cooldown() {
        return this.command.cooldownDuration();
    }

    @Override
    public ChronoUnit cooldownUnit() {
        return this.command.cooldownUnit();
    }

    @Override
    public boolean inherited() {
        return this.command.inherit();
    }

    @Override
    public boolean extended() {
        return this.command.extend();
    }

    @Override
    public boolean confirmation() {
        return this.command.confirm();
    }

    @Override
    public Class<?> parent() {
        return this.command.parent();
    }

    @Override
    public List<CommandElement<?>> elements() {
        return this.elements;
    }
}
