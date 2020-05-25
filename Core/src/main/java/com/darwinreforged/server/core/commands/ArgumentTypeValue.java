package com.darwinreforged.server.core.commands;

import com.darwinreforged.server.core.commands.CommandBus.Arguments;

public abstract class ArgumentTypeValue<T> {
    protected T element;
    protected String permission;

    public ArgumentTypeValue(Arguments argument, String permission, String key) {
        this.permission = permission;
        this.element = parseArgument(argument, key);
    }

    protected abstract T parseArgument(Arguments argument, String key);

    public abstract T getArgument();
}
