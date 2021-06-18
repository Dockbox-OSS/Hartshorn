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
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.commands.registration.MethodCommandContext;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.AbstractFlagCollection;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;
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
    private final List<AbstractArgumentElement<?>> elements;

    public DecoratorCommandContainerContext(Command command) {
        this.command = command;
        this.permission = this.getPermissionOrDefault();
        if (!"".equals(this.arguments()))
            this.elements = this.parseArgumentElements(this.arguments(), permission.get());
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
    
    protected List<AbstractArgumentElement<?>> parseArgumentElements(CharSequence argString, String defaultPermission) {
        List<AbstractArgumentElement<?>> elements = HartshornUtils.emptyList();
        AbstractFlagCollection<?> flagCollection = null;

        Matcher genericArgumentMatcher = GENERIC_ARGUMENT.matcher(argString);
        while (genericArgumentMatcher.find()) {

            String part = genericArgumentMatcher.group();
            Matcher argumentMatcher = ARGUMENT.matcher(part);
            if (argumentMatcher.matches()) {
                this.extractArguments(elements, argumentMatcher, defaultPermission);

            }
            else {
                Matcher flagMatcher = FLAG.matcher(part);
                flagCollection = this.getAbstractFlagCollection(flagCollection, flagMatcher, defaultPermission);
            }
        }

        /*
        Certain platforms may require the flag collection to be parsed together with the wrapped arguments. It is
        possible that a platform implementation returns a flat list of arguments and flags here, though to avoid
        incompatibilities the option to build and combine these is provided.
        */
        if (null == flagCollection) return elements;
        else return flagCollection.buildAndCombines(this.wrapElements(elements));
    }

    @Override
    public boolean matches(String command) {
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
    public List<AbstractArgumentElement<?>> elements() {
        return this.elements;
    }
}
