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

package org.dockbox.hartshorn.demo.commands.services;

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import java.util.Arrays;

/**
 * A simple service capable of handling sub/child commands. {@link Command} is an extension of {@link Service}.
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
