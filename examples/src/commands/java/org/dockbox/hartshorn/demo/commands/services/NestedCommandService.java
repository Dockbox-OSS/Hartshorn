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
    public void child(CommandContext context) {
        Integer[] ints = context.get("ints");
        int sum = Arrays.stream(ints).mapToInt(Integer::intValue).sum();
        context.applicationContext().log().info("Received: %s, sum of all elements is: %s".formatted(Arrays.toString(ints), sum));
    }

    /**
     * The method activated when the command {@code parent child deep <number>} is correctly entered.
     */
    @Command(value = "child deep", arguments = "<number{Int}>")
    public void deepChild(CommandContext context) {
        final int number = context.get("number");
        context.applicationContext().log().info("This command was executed as a child command of a child command, argument: %d".formatted(number));
    }
}
