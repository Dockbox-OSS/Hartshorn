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

package org.dockbox.hartshorn.demo.commands.services;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.demo.commands.arguments.User;
import org.dockbox.hartshorn.di.annotations.service.Service;

/**
 * A simple capable of handling commands. Any type annotated with {@link Service} (or an
 * extension of it) is automatically registered to the {@link org.dockbox.hartshorn.commands.CommandGateway}
 * if there are methods annotated with {@link Command}.
 */
@Service
public class CommandService {

    /**
     * The method activated when the command {@code speak <message>} is correctly entered. As there
     * is no value entered for {@link Command#value()} the method name is used instead. The notation
     * below is therefore equal to:
     * <pre>{@code
     * @Command(value="speak", arguments="<message{RemainingString}")
     * public void speak(CommandContext context) { ... }
     * }</pre>
     *
     * <p>{@link Command#arguments()} indicates the arguments which are expected to be present. The way
     * these are defined depends on the {@link org.dockbox.hartshorn.commands.CommandParser} used in
     * the {@link org.dockbox.hartshorn.commands.CommandGateway}.
     * By default, this uses the {@link org.dockbox.hartshorn.commands.CommandParserImpl}, which uses
     * the definition context defined in {@link org.dockbox.hartshorn.commands.context.CommandDefinitionContextImpl}.
     *
     * <p>The type of the {@code message} argument is set to be {@code RemainingString}, which combines
     * all arguments into a single {@link String} value. Simple parameter types can be added by registering
     * a {@link org.dockbox.hartshorn.commands.definition.ArgumentConverter}, as seen in
     * {@link org.dockbox.hartshorn.commands.arguments.DefaultArgumentConverters}. For example,
     * {@code RemainingString} is added by {@link org.dockbox.hartshorn.commands.arguments.DefaultArgumentConverters#REMAINING_STRING}.
     *
     * <p>{@link ArgumentConverterService} adds a custom {@link org.dockbox.hartshorn.commands.definition.ArgumentConverter},
     * which is used in the command {@link #greet(CommandContext)}.
     */
    @Command(arguments = "<message{RemainingString}>")
    public void speak(final CommandContext context) {
        context.applicationContext().log().info(context.get("message"));
    }

    /**
     * The method activated when the command {@code greet <greeting>} is correctly entered. Unlike {@link #speak(CommandContext)},
     * this command accepts a custom argument which has a registered {@link org.dockbox.hartshorn.commands.definition.ArgumentConverter}
     * as seen in {@link ArgumentConverterService#GREETER}. This converter prepends the name entered with 'Hello', so e.g. 'world'
     * becomes 'Hello world'.
     *
     * <p>Additionally, this command accepts a simple flag. Flags are optional arguments which are at the end of any command. Here
     * the flag is of type {@link Boolean}, and is used to indicate whether an exclamation mark should be present at the end of a
     * message. For example, when the flag is absent or set to {@code false}, the input 'world' becomes 'Hello world'. However, if
     * the flag is set to {@code true}, the message becomes 'Hello world!'.
     */
    @Command(arguments = "<greeting{Greeting}> --exclaim Boolean")
    public void greet(final CommandContext context) {
        final Exceptional<CommandParameter<Boolean>> exclaim = context.flag("exclaim");
        final boolean doExclaim = exclaim.present() && exclaim.get().value();
        context.applicationContext().log().info(context.get("greeting") + (doExclaim ? "!" : ""));
    }

    /**
     * The method activated when the command {@code build <user>} is correctly entered. The parameter {@code user} does not have an
     * explicit {@link org.dockbox.hartshorn.commands.definition.ArgumentConverter} and is instead dynamically constructed through a
     * {@link org.dockbox.hartshorn.commands.arguments.CustomParameterPattern}.
     *
     * <p>Parameter patterns allow the construction of more complex types through field injection. Each field in {@link User} is
     * parsed as a separate argument, and is then combined into a new {@link User} instance. In this example the
     * {@link org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern} is used, which means the input should follow the pattern:
     * {@code #user[name][age]}, where {@code name} is a {@link String} and {@code age} is a {@link Integer}.
     *
     * @see User
     */
    @Command(arguments = "<user{User}>")
    public void build(final CommandContext context) {
        final User user = context.get("user");
        context.applicationContext().log().info("Built a new user named %s with age %s".formatted(user.name(), user.age()));
    }
}
