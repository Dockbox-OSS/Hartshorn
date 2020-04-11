package com.darwinreforged.server.core.util.commands.element;

import java.util.List;
import com.darwinreforged.server.core.util.commands.annotation.processor.Param;
import com.darwinreforged.server.core.util.commands.command.CommandException;
import com.darwinreforged.server.core.util.commands.command.Context;
import com.darwinreforged.server.core.util.commands.command.Input;
import com.darwinreforged.server.core.util.commands.element.function.Filter;
import com.darwinreforged.server.core.util.commands.element.function.Options;
import com.darwinreforged.server.core.util.commands.element.function.ValueParser;


public class NodeElement extends ValueElement {

    private final String main;

    public NodeElement(String key, List<String> aliases) {
        super(key, Param.Type.NODE.priority(), Options.of(aliases), Filter.STARTS_WITH, ValueParser.node(aliases));
        this.main = aliases.get(0);
    }

    @Override
    public void parse(Input input, Context context) throws CommandException {
        getParser().parse(input);
    }

    @Override
    public String toString() {
        return "Node: " + main;
    }
}
