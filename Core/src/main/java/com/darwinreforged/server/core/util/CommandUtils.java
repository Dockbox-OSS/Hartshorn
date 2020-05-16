package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.commands.CommandBus.CommandRunner;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.init.AbstractUtility;

@AbstractUtility("Command registration and execution utilities")
public abstract class CommandUtils<S, C> {

    private static final CommandBus bus = new CommandBus();

    public CommandBus getBus() {
        return bus;
    }

    public abstract void executeCommand(CommandSender sender, String command);

    public abstract boolean handleCommandSend(S source, String command);

    public abstract void registerSingleCommand(String command, String permission, CommandRunner runner);

    protected abstract CommandContext convertContext(C ctx);

    public abstract void registerCommandWithSubs(String command, String permission, CommandRunner runner);
}
