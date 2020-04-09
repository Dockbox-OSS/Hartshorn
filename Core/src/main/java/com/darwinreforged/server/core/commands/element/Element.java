package com.darwinreforged.server.core.commands.element;

import java.util.List;
import com.darwinreforged.server.core.commands.command.CommandException;
import com.darwinreforged.server.core.commands.command.Context;
import com.darwinreforged.server.core.commands.command.Input;


public interface Element {

    void parse(Input input, Context context) throws CommandException;

    void suggest(Input input, Context context, List<String> suggestions);

    default int getPriority() {
        return PRIORITY;
    }

    int PRIORITY = 1;

    Element EMPTY = new Element() {
        @Override
        public void parse(Input input, Context context) throws CommandException {}

        @Override
        public void suggest(Input input, Context context, List<String> suggestions) {}
    };
}
