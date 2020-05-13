package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.init.AbstractUtility;

/**
 The type Command utils.

 @param <S>
 the type parameter
 */
@AbstractUtility("Command registration and execution utilities")
public abstract class CommandUtils<S> {

    private static final CommandBus bus = new CommandBus();

    /**
     Gets bus.

     @return the bus
     */
    public CommandBus getBus() {
        return bus;
    }

    /**
     Execute command.

     @param sender
     the sender
     @param command
     the command
     */
    public abstract void executeCommand(CommandSender sender, String command);

    /**
     Handle command send boolean.

     @param source
     the source
     @param command
     the command

     @return the boolean
     */
    public abstract boolean handleCommandSend(S source, String command);

}
