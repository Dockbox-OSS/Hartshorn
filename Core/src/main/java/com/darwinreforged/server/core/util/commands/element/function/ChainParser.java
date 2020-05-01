package com.darwinreforged.server.core.util.commands.element.function;

import com.darwinreforged.server.core.util.commands.command.CommandException;
import com.darwinreforged.server.core.util.commands.command.Input;


public interface ChainParser<D, T> {

    T map(Input input, D d) throws CommandException;
}
