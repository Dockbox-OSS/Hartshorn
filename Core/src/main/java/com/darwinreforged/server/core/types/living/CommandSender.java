package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

public abstract class CommandSender extends Target implements MessageReceiver {

    public abstract void explainCommand(String message, Command command);

    public abstract boolean hasPermission(Permissions permission);
}
