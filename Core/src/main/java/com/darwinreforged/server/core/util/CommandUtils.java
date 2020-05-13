package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.init.AbstractUtility;

@AbstractUtility("Command registration and execution utilities")
public abstract class CommandUtils<S> {

    private static final CommandBus bus = new CommandBus();

    public CommandBus getBus() {
        return bus;
    }

    public abstract void executeCommand(CommandSender sender, String command);

    public abstract boolean handleCommandSend(S source, String command);

}
