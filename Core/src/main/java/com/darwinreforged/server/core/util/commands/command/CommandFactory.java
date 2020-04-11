package com.darwinreforged.server.core.util.commands.command;

import java.util.Collection;


public interface CommandFactory<T extends Command> {

    T create(Collection<String> aliases, Collection<CommandExecutor> executors);
}
