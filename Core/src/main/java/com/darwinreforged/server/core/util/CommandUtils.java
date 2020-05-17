package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.commands.CommandBus.CommandRunner;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.init.AbstractUtility;

import java.util.regex.Pattern;

@AbstractUtility("Command registration and execution utilities")
public abstract class CommandUtils<S, C> {

    protected static final Pattern argFinder = Pattern.compile("((?:<.+?>)|(?:\\[.+?\\])|(?:-(?:(?:-\\w+)|\\w)(?: [^ -]+)?))"); //each match is a flag or argument
    protected static final Pattern flag = Pattern.compile("-(-?\\w+)(?: ([^ -]+))?"); //g1: name  (g2: value)
    protected static final Pattern argument = Pattern.compile("([\\[<])(.+)[\\]>]"); //g1: <[  g2: run argFinder, if nothing it's a value
    protected static final Pattern value = Pattern.compile("(\\w+)(?:\\{(\\w+)(?::([\\w\\.]+))?\\})?"); //g1: name  g2: if present type, other wise use g1
    protected static final Pattern subcommand = Pattern.compile("[a-z]+");

    private static final CommandBus bus = new CommandBus();

    public CommandBus getBus() {
        return bus;
    }

    @Deprecated
    public abstract void executeCommand(CommandSender sender, String command);

    @Deprecated
    public abstract boolean handleCommandSend(S source, String command);

    public abstract void registerCommandNoArgs(String command, String permission, CommandRunner runner);

    protected abstract CommandContext convertContext(C ctx);

    public abstract void registerCommandArgsAndOrChild(String command, String permission, CommandRunner runner);
}
