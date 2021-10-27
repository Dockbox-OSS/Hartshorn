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

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;

import java.util.Arrays;

/**
 * A simple service capable of handling sub/child commands. {@link Command} is an extension of {@link org.dockbox.hartshorn.di.annotations.service.Service}.
 * Services annotated with {@link Command} are able to register child commands, as each {@link Command} method in such a service
 * is registered as a child command.
 */
@Command("parent")
public class NestedCommandService {

    /**
     * The method activated when the command {@code parent child <ints>} is correctly entered.
     */
    @Command(arguments = "<ints{RemainingInt}>")
    public void child(final CommandContext context) {
        final Integer[] ints = context.get("ints");
        final int sum = Arrays.stream(ints).mapToInt(Integer::intValue).sum();
        context.applicationContext().log().info("Received: %s, sum of all elements is: %s".formatted(Arrays.toString(ints), sum));
    }

    /**
     * The method activated when the command {@code parent child deep <number>} is correctly entered.
     */
    @Command(value = "child deep", arguments = "<number{Int}>")
    public void deepChild(final CommandContext context) {
        final int number = context.get("number");
        context.applicationContext().log().info("This command was executed as a child command of a child command, argument: %d".formatted(number));
    }
}
